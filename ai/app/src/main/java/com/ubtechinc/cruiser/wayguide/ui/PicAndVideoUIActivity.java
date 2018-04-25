package com.ubtechinc.cruiser.wayguide.ui;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.ubtechinc.cruiser.wayguide.R;
import com.ubtechinc.cruiser.wayguide.event.TaskEvent;
import com.ubtechinc.cruzr.sdk.face.CruzrFaceApi;
import com.ubtechinc.cruzr.sdk.speech.SpeechRobotApi;
import com.ubtechinc.cruzr.serverlibutil.interfaces.SpeechTtsListener;
import com.ubtechinc.framework.notification.NotificationCenter;

import java.util.Timer;
import java.util.TimerTask;

import static com.ubtechinc.cruiser.wayguide.task.impl.WayGuiderPicAndVideoTaskImpl.TYPE_TASK_IMAGE;
import static com.ubtechinc.cruiser.wayguide.task.impl.WayGuiderPicAndVideoTaskImpl.TYPE_TASK_TTS_IMAGE;
import static com.ubtechinc.cruiser.wayguide.task.impl.WayGuiderPicAndVideoTaskImpl.TYPE_TASK_VIDEO;


public class PicAndVideoUIActivity extends Activity {
    private static final String TAG = "PicAndVideoUIActivity";
    private static final String VIDEO_URL = "http://video.ubtrobot.com/CF09D337D659F02434233DC0C3BE01EE.mp4";
    private static final String VIDEO_URL1 = "http://video.ubtrobot.com/0F836F3BF2D8177FE9CA844F46456115.mp4";
    private static final String PIC_URL = "http://video.ubtrobot.com/1F267050C1954D94B3143DF7357154F1.1.bmp";
    private VideoView videoView;

    private ImageView mIvShow;

    private Button mBtnDownload;

    private DownloadManager mDownloadManager;

    private Button mBtnQuery;

    private Button mBtnDel;

    private BroadcastReceiver mVideoReceiver;

    //显示的类型
    private int type;
    //本地图片或视频路径
    private String filePath;
    //显示延迟时间
    private long delayTime=1*1000;
    private String tts;

    private String locName;

    public static PicAndVideoUIActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pic_video);

        init();
    }

    private void init() {
        CruzrFaceApi.initCruzrFace(this);
        CruzrFaceApi.setCruzrFace(null,"clean",false,false);

        mDownloadManager=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);

        initVideoView();

        CruzrFaceApi.initCruzrFace(this);
        CruzrFaceApi.setCruzrFace(null,"clean",false,false);

        type=getIntent().getIntExtra("type",0);
        filePath=getIntent().getStringExtra("path");
        locName=getIntent().getStringExtra("loc");

        switch (type){
            case TYPE_TASK_IMAGE:
                  delayTime=getIntent().getLongExtra("time",0);
                  showImage();
                break;
            case TYPE_TASK_TTS_IMAGE:
                  tts=getIntent().getStringExtra("tts");
                  delayTime=getIntent().getLongExtra("time",0);
                  showImageTTS();
                break;
            case TYPE_TASK_VIDEO:
                  showVideo();
                break;
        }

        instance=this;
    }

    private void showImage(){
        mIvShow.setVisibility(View.VISIBLE);
        Log.e(TAG, "showImage: filePath="+filePath);
        mIvShow.setImageURI(Uri.parse(filePath));

        close(delayTime);
    }

    private void showImageTTS(){
        Log.e(TAG, "showImageTTS: filepath="+filePath);
        mIvShow.setVisibility(View.VISIBLE);
        mIvShow.setImageURI(Uri.parse(filePath));

        SpeechRobotApi.get().speechStartTTS(tts, new SpeechTtsListener() {
            @Override
            public void onAbort() {
                close(delayTime);

            }

            @Override
            public void onEnd() {
                close(delayTime);
            }
        });

    }

    private void showVideo(){
        videoView.setVisibility(View.VISIBLE);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                sendFinishSignal();
            }
        });
        videoView.setVideoPath(filePath);
        videoView.start();
    }

    private void sendFinishSignal() {
        TaskEvent event=new TaskEvent();
        event.in=TaskEvent.TASK_EVENT_FINISH;
        event.name=locName;
        NotificationCenter.defaultCenter().publish(event);
        finish();
    }

    private void close(long delayTime){
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                sendFinishSignal();
            }
        };
        new Timer().schedule(task,delayTime);


    }


    private void videoSuceed(String fileLocalUri){
        videoView.setVisibility(View.VISIBLE);
        mBtnDel.setVisibility(View.GONE);
        mBtnQuery.setVisibility(View.GONE);
        mBtnDownload.setVisibility(View.GONE);

        videoView.setVideoPath(fileLocalUri);
        videoView.start();

    }
    private void  initVideoView(){
        videoView=(VideoView)findViewById(R.id.vv_show);
        mIvShow=(ImageView) findViewById(R.id.iv_show);
    }

    private void handleVideo(){
        videoView=(VideoView)findViewById(R.id.vv_show);
        videoView.setVisibility(View.VISIBLE);

        videoView.setMediaController(new MediaController(this));

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                            Toast.makeText(PicAndVideoUIActivity.this, "play begining..", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }
        });
        videoView.start();
    }



    private String query(String fileUrl){
        String rst="";
        Uri uri = null;
        DownloadManager.Query query = new DownloadManager.Query();

        DownloadManager.Query baseQuery = query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);


        Cursor cursor = null;
        try {
            cursor = mDownloadManager.query(baseQuery);
            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String url = cursor.getString(
                            cursor.getColumnIndex(DownloadManager.COLUMN_URI));
                    int status = cursor.getInt(
                            cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                    System.out.println("url:" + url+">id="+cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID)));
                    if (url != null && url.equals(fileUrl)) {
                        String fileUri = cursor.getString(
                                cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        System.out.println("local_uri:" + fileUri);
                        //TODO 检查下载文件是否存在
                        if (fileUri != null) {
                              rst=fileUri;
                        }
                    }

                }
            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
            Log.i("localapkuri", "ex="+ex.toString());
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return rst;
    }

    public static final String AUTHORITY = "com.android.app.downloads";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/my_downloads");

    private void delete(){
        ContentResolver mResolver=this.getContentResolver();
        mResolver.delete(CONTENT_URI,null,null);
    }

    private void deleteById(long id){
        mDownloadManager.remove(id);
    }

    private void addObserveVideoDownload(){
        mVideoReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    Cursor cursor = mDownloadManager.query(query);
                    if (cursor.moveToFirst()) {
                        String url = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
                        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        final String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        if(status== DownloadManager.STATUS_SUCCESSFUL) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    videoSuceed(fileUri);
                                }
                            });
                        }
                    }
                }
            }
        };
        IntentFilter intentFilter=new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(mVideoReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        instance=null;
        if(type==TYPE_TASK_VIDEO){
            videoView.stopPlayback();
        }
    }


}
