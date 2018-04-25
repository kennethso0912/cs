package com.ubtechinc.cruiser.wayguide.ui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.ubtechinc.cruiser.wayguide.R;
import com.ubtechinc.cruiser.wayguide.app.AppApplication;
import com.ubtechinc.cruiser.wayguide.event.TaskEvent;
import com.ubtechinc.cruiser.wayguide.model.Media;
import com.ubtechinc.cruiser.wayguide.task.IActionTask;
import com.ubtechinc.cruiser.wayguide.task.impl.ActionTaskImpl;
import com.ubtechinc.cruiser.wayguide.utils.UbtConstant;
import com.ubtechinc.cruzr.sdk.face.CruzrFaceApi;
import com.ubtechinc.cruzr.sdk.speech.SpeechRobotApi;
import com.ubtechinc.cruzr.serverlibutil.interfaces.SpeechTtsListener;
import com.ubtechinc.framework.notification.NotificationCenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import static com.ubtechinc.cruiser.wayguide.task.impl.WayGuiderPicAndVideoTaskImpl.TYPE_TASK_TTS;

/**
 * Created on 2017/12/21.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class TTsService extends Service{
    private static final String TAG = "TTsService";
    //显示延迟时间
    private long delayTime=1*1000;
    private String locName;
    private ArrayList<Media> medias;
    private Media mCurMedia;
    private String tts;
    //是否正常结束TTS媒体播放关闭界面
    private volatile boolean isNormalFinish=false;
    private Timer mTimer=new Timer();

    //动作任务
    private IActionTask mActionTask=new ActionTaskImpl();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        init();
    }

    private void init() {
        getData();

        AppApplication.addService(locName,this);

        forbiddenFace();

        handleTTsData();
    }

    private void getData(){

//        this.medias=getIntent().getParcelableArrayListExtra("medias");
        this.medias=AppApplication.getMediasByTaskName(this.locName);
    }

    private void forbiddenFace() {
        CruzrFaceApi.initCruzrFace(this);
        CruzrFaceApi.setCruzrFace(null,"clean",true,true);
    }

    /**
     * 处理当前的TTS媒体数据
     */
    private void  handleTTsData(){

        if(medias.isEmpty()){//不可达到
            Log.e(TAG, "handleTTsData: 语音界面不可达到地方");
        }else{
            this.mCurMedia=medias.get(0);

            int type=mCurMedia.getType();
            if(type==TYPE_TASK_TTS){
                this.tts=mCurMedia.getTts();
            }else {//没加载到视频和图片失败的TTS
                int mediaNameId=getResources().getIdentifier("media_type_"+type,"string",AppApplication.getContext().getPackageName());
                String mediaName=getString(mediaNameId);
                this.tts=getString(R.string.pic_video_fail_tts_new,mediaName);
            }

            mActionTask.play();

            Log.e(TAG, "handleTTsData: 开始调用TTS："+tts);

            SpeechRobotApi.get().speechStartTTS(tts, new SpeechTtsListener() {
                @Override
                public void onAbort() {
                    Log.e(TAG, "onAbort: tts->"+tts);
                    close(delayTime);

                }

                @Override
                public void onEnd() {
                    Log.e(TAG, "onEnd: tts->"+tts);
                    close(delayTime);
                }
            });

        }

    }

    /**
     * 延迟关闭当前媒体播放 可以考虑和图片视频媒体界面合并整合
     * @param delayTime
     */
    private void close(long delayTime){
        mActionTask.stop();

        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                handleCurMediaEnd();
            }
        };
        mTimer.schedule(task,delayTime);

    }

    /**
     * 当前媒体的结束处理 可以考虑和图片视频媒体界面合并整合
     */
    private void handleCurMediaEnd() {
        medias.remove(mCurMedia);
        Log.e(TAG, "run: medias.isEmpty():"+medias.isEmpty());
        if(medias.isEmpty()){
            TTsService.this.isNormalFinish=true;

            Log.e(TAG, "延时TTS界面结束关闭界面 finish()->");
//            finish();

            //一个游览点的所有媒体任务结束，发消息回游览队列结点，进行时间的超时和状态的关闭
            sendFinishedSignalOfLastMedia();
        }else {
            handleNextMedia();
        }
    }

    private void sendFinishedSignalOfLastMedia() {
        TaskEvent event=new TaskEvent();
        event.in=TaskEvent.TASK_EVENT_FINISH;
        event.name=locName;
        NotificationCenter.defaultCenter().publish(event);
    }

    /**
     * 处理下一个媒体数据
     */
    private void handleNextMedia(){
        Media media=medias.get(0);
        String action=media.getMediaAction();

        if(action!=UbtConstant.INTENT_TTS) {
            TTsService.this.isNormalFinish=true;

            Log.e(TAG, "TTS界面正常关闭即将进行图片和视频界面播放 finish()->");

            stopSelf();

            Intent intent = new Intent(action);
            intent.putExtra("taskName", locName);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else{
            handleTTsData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()->TTsService.this.isNormalFinish="+TTsService.this.isNormalFinish);

        if(!TTsService.this.isNormalFinish) {
            if(!medias.contains(mCurMedia)) {
                medias.add(0, mCurMedia);//出现TTS被打断时无法记忆的补充
            }
            stop();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.locName=intent.getStringExtra("taskName");

        init();
        return super.onStartCommand(intent, flags, startId);
    }



    /**
     * 停止当前媒体播放
     */
    private void stop(){
        SpeechRobotApi.get().speechStopTTS();//与闲聊冲突暂时屏蔽
        mTimer.cancel();
        mActionTask.stop();
    }
}
