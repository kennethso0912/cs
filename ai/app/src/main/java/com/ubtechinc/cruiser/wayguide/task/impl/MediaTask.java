package com.ubtechinc.cruiser.wayguide.task.impl;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.ubtechinc.cruiser.wayguide.app.AppApplication;
import com.ubtechinc.cruiser.wayguide.event.TaskEvent;
import com.ubtechinc.cruiser.wayguide.model.Media;
import com.ubtechinc.cruiser.wayguide.presenter.impl.WayGuiderPresenterImpl;
import com.ubtechinc.cruiser.wayguide.service.ServiceProxy;
import com.ubtechinc.cruiser.wayguide.task.IMediaTask;
import com.ubtechinc.cruiser.wayguide.utils.UbtConstant;
import com.ubtechinc.framework.notification.NotificationCenter;
import com.ubtechinc.framework.notification.Subscriber;

import java.util.ArrayList;

import static com.ubtechinc.cruiser.wayguide.service.ServiceProxy.SERVICE_WAY_GUIDER;


/**
 * Created on 2017/7/5.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class MediaTask implements IMediaTask {
    private static final String TAG = "MediaTask";

    private String taskName;
    private Subscriber<TaskEvent> mSub;

    public MediaTask(Subscriber<TaskEvent> mSub,String taskName){
           this.mSub=mSub;
           this.taskName=taskName;
    }


    @Override
    public void start() {
        ArrayList<Media> medias;
        Log.e(TAG, "start: this.taskName"+this.taskName);

        //出现在游览点开始游览时立即点击“结束游览”按钮
        WayGuiderPresenterImpl wpl= (WayGuiderPresenterImpl)ServiceProxy.getPresenter(SERVICE_WAY_GUIDER);
        if(!wpl.isStarted()){
             return;
        }

        if (!TextUtils.isEmpty(this.taskName)){
             medias=AppApplication.getMediasByTaskName(this.taskName);

            Log.e(TAG, "start: medias"+medias);
            if (medias!=null&&!medias.isEmpty()){
               Media media=medias.get(0);
               handleMedia(media);
            }
        }
    }

    @Override
    public void stop() {}


    /**
     * 多媒体类型任务展示
     * @param  @media媒体的类型：1 图片 2 视频 3 TTS+图片
     */
    private void handleMedia(Media media){
        regEvent();

        String action = media.getMediaAction();

        Log.e(TAG, "handleMedia: action="+action);
        goToSpecifiedMedia(action);
    }



//    private void goToSpecifiedMedia(String action) {
//        Intent intent=new Intent(action);
//        intent.putExtra("taskName",taskName);
////        intent.putParcelableArrayListExtra("medias",medias);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        AppApplication.getContext().startActivity(intent);
//    }

    private void goToSpecifiedMedia(String action) {
        Intent intent=new Intent(action);
        intent.putExtra("taskName",taskName);
//        intent.putParcelableArrayListExtra("medias",medias);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//        AppApplication.getContext().startActivity(intent);//原来是activity tts 现在改成service tts
        if(UbtConstant.INTENT_TTS.equals(action)){
            intent.setPackage(AppApplication.getContext().getPackageName());
            AppApplication.getContext().startService(intent);
        }else {
            AppApplication.getContext().startActivity(intent);
        }
    }

    /**
     * 开始注册接收所有媒体播放结束的消息订阅
     */
    private void regEvent(){
        NotificationCenter.defaultCenter().subscriber(TaskEvent.class,mSub);
    }



}
