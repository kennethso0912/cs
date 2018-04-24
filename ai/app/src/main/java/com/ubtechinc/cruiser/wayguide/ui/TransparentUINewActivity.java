package com.ubtechinc.cruiser.wayguide.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

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
import java.util.Timer;
import java.util.TimerTask;

import static com.ubtechinc.cruiser.wayguide.task.impl.WayGuiderPicAndVideoTaskImpl.TYPE_TASK_TTS;


public class TransparentUINewActivity extends Activity {
    private static final String TAG = "TransparentUIActivity";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        init();

        Log.e("ken", "onCreate: taskName="+locName+";TransparentUIActivity="+this);
    }

    private void init() {
        setContentView(R.layout.tts_status_btn);

        getData();

        AppApplication.addActivity(locName,this);

        forbiddenFace();

        handleTTsData();
    }

    private void forbiddenFace() {
        CruzrFaceApi.initCruzrFace(this);
        CruzrFaceApi.setCruzrFace(null,"clean",true,true);
    }


    private void getData(){
        this.locName=getIntent().getStringExtra("taskName");
//        this.medias=getIntent().getParcelableArrayListExtra("medias");
        this.medias=AppApplication.getMediasByTaskName(this.locName);
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
            TransparentUINewActivity.this.isNormalFinish=true;

            Log.e(TAG, "延时TTS界面结束关闭界面 finish()->");
            finish();

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
            TransparentUINewActivity.this.isNormalFinish=true;

            Log.e(TAG, "TTS界面正常关闭即将进行图片和视频界面播放 finish()->");

            finish();

            Intent intent = new Intent(action);
            intent.putExtra("taskName", locName);
//            intent.putParcelableArrayListExtra("medias", medias);

            startActivity(intent);
        }else{
            handleTTsData();
        }
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()->");

        if(!TransparentUINewActivity.this.isNormalFinish) {
            stop();
        }
    }

    /**
     * w停止当前媒体播放
     */
    private void stop(){
        SpeechRobotApi.get().speechStopTTS();//与闲聊冲突暂时屏蔽
        mTimer.cancel();
    }
}
