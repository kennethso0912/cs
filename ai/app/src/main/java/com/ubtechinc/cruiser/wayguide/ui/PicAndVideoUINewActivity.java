package com.ubtechinc.cruiser.wayguide.ui;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.ubtechinc.cruiser.wayguide.R;
import com.ubtechinc.cruiser.wayguide.app.AppApplication;
import com.ubtechinc.cruiser.wayguide.download.DownloadUtil;
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

import static com.ubtechinc.cruiser.wayguide.task.impl.WayGuiderPicAndVideoTaskImpl.TYPE_TASK_IMAGE;
import static com.ubtechinc.cruiser.wayguide.task.impl.WayGuiderPicAndVideoTaskImpl.TYPE_TASK_TTS;
import static com.ubtechinc.cruiser.wayguide.task.impl.WayGuiderPicAndVideoTaskImpl.TYPE_TASK_TTS_IMAGE;
import static com.ubtechinc.cruiser.wayguide.task.impl.WayGuiderPicAndVideoTaskImpl.TYPE_TASK_VIDEO;


public class PicAndVideoUINewActivity extends Activity {
    private static final String TAG = "PicAndVideoUIActivity";
    private static final int TIME_SYS_BAR_HIDE = 1000;
    private VideoView mVideoView;
    private ImageView mIvShow;
    private FrameLayout mFlVideo;

    //显示的类型
    private int type;
    //本地图片或视频路径
    private String filePath;
    //显示延迟时间
    private long delayTime=10*1000;
    private String tts;
    private String locName;
    public static PicAndVideoUINewActivity instance;
    private Timer mTimer;
    //正常执行完的关闭
    private volatile boolean isNormalFinish=false;
    //当前游览点的所有媒体列表
    private ArrayList<Media> mMedias=new ArrayList<>();
    private Media mCurMedia;

    private IActionTask mActionTask=new ActionTaskImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_video);

        init();

        Log.e("ken", "onCreate: taskName="+locName+";PicAndVideoUINewActivity="+this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        hideSysMenuBar();
    }

    @Override
    protected void onStop() {
        super.onStop();
        showSysMenuBar();
    }

    private void init(){
        mTimer=new Timer();

        initVideoView();

        getData();

        handleCurMediaData();

        AppApplication.addActivity(locName,this);

    }

    private void getData(){
        this.locName=getIntent().getStringExtra("taskName");
        this.mMedias=AppApplication.getMediasByTaskName(this.locName);
    }


    private void showImage(){
        forbiddenFace();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                hideSysMenuBar();
                mVideoView.stopPlayback();
//                videoView.setVisibility(View.GONE);
                mFlVideo.setVisibility(View.GONE);

                mIvShow.setVisibility(View.VISIBLE);
                Log.e(TAG, "showImage: filePath="+filePath);
//                mIvShow.setImageURI(Uri.parse(filePath));

                processImageViewSize(filePath);
                ImageLoader.getInstance().displayImage(filePath,mIvShow,OPTIONS);
            }
        });

    }

    private void showImageTTS(){
        showImage();

        mActionTask.play();

        Log.e(TAG, "showImageTTS: 开始调用tts="+tts+";delayTime="+delayTime);
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

    private void showVideo(){
        forbiddenFace();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                hideSysMenuBar();

                mIvShow.setVisibility(View.GONE);

                mFlVideo.setVisibility(View.VISIBLE);
//                videoView.setVisibility(View.VISIBLE);

                mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.e(TAG, "onCompletion: 视频播放完毕");
                          handleCurMediaEnd();
                    }
                });
                mVideoView.setVideoPath(filePath);
                mVideoView.start();
            }
        });

    }


    /**
     * 图片和TTS的延迟处理
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

        mTimer.schedule(task,delayTime*1000);

    }

    /**
     * 当前媒体的结束处理
     */
    private void handleCurMediaEnd() {
        mMedias.remove(mCurMedia);

        Log.e(TAG, "mMedias.isEmpty(): "+mMedias.isEmpty()+";mMedias.size():"+mMedias.size());
        if(mMedias.isEmpty()){
            PicAndVideoUINewActivity.this.isNormalFinish=true;

            Log.e(TAG, "延时结束关闭界面  finish()->");
            finish();
            //一个游览点的所有媒体任务结束，发消息回游览队列结点，进行时间的超时和状态的关闭
            TaskEvent event=new TaskEvent();
            event.in=TaskEvent.TASK_EVENT_FINISH;
            event.name=locName;
            NotificationCenter.defaultCenter().publish(event);
        }else {
            handleNextMedia();
        }
    }


    private void  initVideoView(){
        mFlVideo=(FrameLayout) findViewById(R.id.fl_video);

        mVideoView=(VideoView)findViewById(R.id.vv_show);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVolume(1.0F,1.0F);
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            Log.e(TAG, "onInfo: 视频开始播放");
                        }
                        return true;
                    }
                });
            }
        });
        mIvShow=(ImageView) findViewById(R.id.iv_show);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()->");

        instance=null;
        if(type==TYPE_TASK_VIDEO){
            mVideoView.stopPlayback();
        }
//        showSysMenuBar();

        if(!PicAndVideoUINewActivity.this.isNormalFinish) {
            if(!mMedias.contains(mCurMedia)) {
                mMedias.add(0, mCurMedia);//出现TTS被打断时无法记忆的补充
            }
            stop();
        }
    }


    /**
     * 处理下一个媒体数据
     */
    private void handleNextMedia(){
        Media media=this.mMedias.get(0);

        String action=media.getMediaAction();
        if(action==UbtConstant.INTENT_TTS) {
            PicAndVideoUINewActivity.this.isNormalFinish=true;

            Log.e(TAG, "界面正常关闭即将进行TTS无界面播放 finish()->");
            finish();

            Intent intent = new Intent(action);
            intent.putExtra("taskName", locName);
//            intent.putParcelableArrayListExtra("medias", medias);

//            startActivity(intent);//原来是activity tts 现在改成service tts

            intent.setPackage(getPackageName());
            startService(intent);
        }else{
            handleCurMediaData();
        }
    }

    /**
     * 当前媒体数据的处理
     */
    private void handleCurMediaData(){
        forbiddenFace();


        if(mMedias==null||mMedias.isEmpty()){
            //当前为空不会跳过来的，这里是无效的
            Log.e(TAG, "handleMediaData: 界面已存在，替换数据处理->当前为空不会跳过来的，这里是无效的");

        }else{
            mCurMedia=mMedias.get(0);
            type=mCurMedia.getType();

            filePath= DownloadUtil.getLocalFilePathByUrl(AppApplication.getContext(),this.mCurMedia.getImgUrl());
            switch (type){
                case TYPE_TASK_IMAGE:
                    delayTime=mCurMedia.getImgPlayTime();
                    showImage();
                    close(delayTime);
                    break;
                case TYPE_TASK_TTS_IMAGE:
                    tts=mCurMedia.getTts();
                    delayTime=mCurMedia.getImgPlayTime();
                    showImageTTS();
                    break;
                case TYPE_TASK_VIDEO:
                    filePath= DownloadUtil.getLocalFilePathByUrl(AppApplication.getContext(),this.mCurMedia.getVideoUrl());
                    showVideo();
                    break;
                default:
                    break;
            }

        }
    }

    private void forbiddenFace() {
        CruzrFaceApi.initCruzrFace(this);
        CruzrFaceApi.setCruzrFace(null,"clean",false,false);
    }


    /**
     * 关闭系统菜单条
     */
    private void hideSysMenuBar(){
        Handler handler=new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendBroadcast(new Intent("com.ubt.cruzr.hidebar"));
            }
        },TIME_SYS_BAR_HIDE);

    }

    /**
     * 显示系统菜单条
     */
    private void showSysMenuBar(){
        sendBroadcast(new Intent("com.ubt.cruzr.showbar"));
    }


    /**
     * 停止当前媒体数据的执行
     */
    private void stop(){
        if(PicAndVideoUINewActivity.this.type==TYPE_TASK_TTS_IMAGE){
            SpeechRobotApi.get().speechStopTTS();//与闲聊冲突暂时屏蔽
            mTimer.cancel();
            mActionTask.stop();
        }else if(PicAndVideoUINewActivity.this.type==TYPE_TASK_VIDEO){
            mVideoView.stopPlayback();
        }else if(PicAndVideoUINewActivity.this.type==TYPE_TASK_TTS){
            mTimer.cancel();
        }
    }

    public void jumpToNext(View view){
        if (mVideoView!=null&&mVideoView.isPlaying()){
            mVideoView.stopPlayback();
            handleCurMediaEnd();
        }
    }

    private static final DisplayImageOptions OPTIONS = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .displayer(new RoundedBitmapDisplayer(0)).build();

    private void processImageViewSize(String filePath){
        Bitmap bitmap= BitmapFactory.decodeFile(Uri.parse(filePath).getPath());

        if(bitmap==null){
            return;
        }

        int height= bitmap.getHeight();
        int width= bitmap.getWidth();
        bitmap.recycle();

        if(width>=1920&&height>=1080){
            return;
        }

        if(height>=width){
            int tempWidth=width*1080/height;
            if(tempWidth>1920){
                height=height*1920/width;
                width=1920;
            }else {
                width=tempWidth;
                height = 1080;
            }
        }else if(height<width){
            int tempHeight=height*1920/width;
            if(tempHeight>1080){
                width=width*1080/height;
                height=1080;
            }else {
                height=tempHeight;
                width = 1920;
            }
        }


        Log.e(TAG, "setView: h:"+height+"w:"+width);
        LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams(width,height);
        lp.setMargins(-1,-1,-1,-1);

        mIvShow.setPadding(-1,-1,-1,-1);
        mIvShow.setLayoutParams(lp);

    }

}
