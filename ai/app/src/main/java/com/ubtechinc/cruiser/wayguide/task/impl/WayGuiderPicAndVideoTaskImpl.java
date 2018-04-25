package com.ubtechinc.cruiser.wayguide.task.impl;



import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import java.util.concurrent.locks.ReentrantLock;

import com.ubtechinc.cruiser.wayguide.ui.PicAndVideoUIActivity;
import com.ubtechinc.cruiser.wayguide.R;
import com.ubtechinc.cruiser.wayguide.app.AppApplication;
import com.ubtechinc.cruiser.wayguide.callback.ITaskCallback;
import com.ubtechinc.cruiser.wayguide.callback.ITtsTaskCallback;
import com.ubtechinc.cruiser.wayguide.download.DownloadUtil;
import com.ubtechinc.cruiser.wayguide.event.Event;
import com.ubtechinc.cruiser.wayguide.event.TaskEvent;
import com.ubtechinc.cruiser.wayguide.map.MapInfo;
import com.ubtechinc.cruiser.wayguide.model.TaskIdleTimeoutChecker;
import com.ubtechinc.cruiser.wayguide.model.WayGuiderManager;
import com.ubtechinc.cruiser.wayguide.task.IActionTask;
import com.ubtechinc.cruiser.wayguide.task.IGoTask;
import com.ubtechinc.cruiser.wayguide.task.ISayTask;
import com.ubtechinc.cruiser.wayguide.task.IWayGuiderTask;
import com.ubtechinc.cruiser.wayguide.utils.SettingsDataUtil;
import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;
import com.ubtechinc.cruzr.serverlibutil.aidl.Position;
import com.ubtechinc.framework.log.NLog;
import com.ubtechinc.framework.notification.NotificationCenter;
import com.ubtechinc.framework.notification.Subscriber;
import static com.ubtechinc.cruiser.wayguide.utils.ParserUtil.getString;
import static com.ubtechinc.cruiser.wayguide.utils.UbtConstant.TTS_RESULT_BROKEN;


/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc  游览任务实现类
 */
public class WayGuiderPicAndVideoTaskImpl implements IWayGuiderTask, TaskIdleTimeoutChecker.IdleTimeoutCallback{
    private final static String TAG="WayGuiderTask";
    private static final float OFF_SET = 0.5f;
    private final static int WAY_GUIDER_SUCCESS = 1;
    private final static int WAY_GUIDER_START = 0;
    private IGoTask mGoTask;
    private ISayTask mSayTask;
    private IActionTask mActionTask;
    private MapInfo.Location loc;
    private volatile int status=WAY_GUIDER_START;
    private final ReentrantLock mLock=new ReentrantLock(true);
    private volatile TaskIdleTimeoutChecker mIdleChecker;
    private volatile boolean isStop=false;
    private final ReentrantLock mStopLock=new ReentrantLock(true);
    private volatile boolean isTtsBroken=false;//当前讲解TTS未播完被打断

    public static final int  TYPE_TASK_TTS= 0;
    public static final int  TYPE_TASK_IMAGE= 1;
    public static final int  TYPE_TASK_VIDEO= 2;
    public static final int  TYPE_TASK_TTS_IMAGE= 3;



    public WayGuiderPicAndVideoTaskImpl(MapInfo.Location loc)
    {
        this.loc=loc;
//        this.mGoTask=new GoSimulateTaskImpl(loc.getX(),loc.getY());//模拟导航类测试
        this.mSayTask=new SayTaskImpl(loc.getContent());

    }

    private void regEvent(){
        NotificationCenter.defaultCenter().subscriber(TaskEvent.class,mSub);
    }

    private void unRegEvent(){
        NotificationCenter.defaultCenter().unsubscribe(TaskEvent.class,mSub);
        mSub=null;
    }


    private synchronized void finish(){
        Log.e(TAG, this.getName()+"->finish（）: ->"+this.toString());
        this.status=WAY_GUIDER_SUCCESS;
    }

    @Override
    public  void start() {
        if(loc==null) {
            return;
        }

        synchronized (WayGuiderPicAndVideoTaskImpl.this.mStopLock){
            WayGuiderPicAndVideoTaskImpl.this.isStop=false;
        }
        
        //判断目前是否同一个点，是，直接关闭当前游览点任务，以防止到达游览点后被暂停后再说继续游览，会重走和重说一遍
        if(isCurPos(loc)){//之前没讲解完被打断后，重播一次讲解
              Log.e(TAG, "判断目前是否处于当前点: 是->"+loc.getName());

              //判断上一次讲解是否被打断过
              if(isTtsBroken){
                  isTtsBroken=false;

                  //由于此时是由"继续游览"进入的，延迟让讲解内容衔接继续游览播报
                  try {
                      Thread.sleep(3000);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }

                  handleLocTask();

              }else{//没被打断过讲解内容，在当前点直接跳过，不播报下一个点的继续游览
                  finish();
                  //最后一个点被打断后继续游览的状态关闭和播报
                  handleLastPoint();
              }

            return;
        }else {

            go(new ITaskCallback() {
                @Override
                public void onFinish() {
                    Log.e(TAG, "onFinish: 当前游览点｛" + loc.getName() + "｝导航正常结束->下面开始游览点讲解");

                    //游览的真正讲解任务
                    handleLocTask();

                }
            });
        }

    }

    private void go(ITaskCallback listener) {
        synchronized (WayGuiderPicAndVideoTaskImpl.class) {
            try {
                Thread.sleep(2000);//延长开启导航时间防止获取当前位置不准的问题
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "导航开始->x:"+loc.getX()+";y:"+loc.getY());
            mGoTask.go(listener);
        }
    }

    private void action(String actName,ITaskCallback listener) {
        mActionTask.play(actName,listener);
    }

    @Override
    public  synchronized boolean isFinish() {
        return this.status==WAY_GUIDER_SUCCESS;
    }

    @Override
    public String getName() {
        return loc.getName();
    }

    @Override
    public synchronized void setStatus(int status) {
           Log.e(TAG, this.getName()+"->setStatus: "+status);
           this.status=status;
    }

    @Override
    public ISayTask getSpeaker() {
          mSayTask.init();
        return mSayTask;
    }

    @Override
    public IGoTask getWalker() {
        return mGoTask;
    }

    @Override
    public void introAgain() {
        if(isFinish()){
            Log.e(TAG, "introAgain: 游览点已结束不能再介绍一遍了");
            return;
        }

        synchronized (mLock) {
            //防止还未到当前点就被问及再介绍一遍而进行播报
            if (mIdleChecker == null) {
                 Log.e(TAG, "introAgain: 还未开始讲解 不能再介绍一遍->"+loc.getName());
                return;
            }

            if (mIdleChecker != null) {
                mIdleChecker.close();
            }
        }

        Log.e(TAG, "introAgain: start再介绍一遍开始->"+this.loc.getName());

        getSpeaker().play("好的，这次可要听清楚了哦", new ITtsTaskCallback() {
            @Override
            public void onFinish(int code) {
                    handleLocTask();
            }
        });

    }

    /**
     * 处理到达游览点的各种任务
     */
    private void handleLocTask(){
        int type=this.loc.getType();
        switch (type){
            case TYPE_TASK_TTS:
                   getSpeaker().play(getString(R.string.intro_tip,loc.getContent(), SettingsDataUtil.get().getWakeupWord(getString(R.string.default_robot_name))),mTtsTaskCb);
                break;
            case TYPE_TASK_IMAGE:
                   showImage();
                break;
            case TYPE_TASK_VIDEO:
                   showVideo();
                break;
            case TYPE_TASK_TTS_IMAGE:
                   showImageTTS();
                break;
            default:
                break;
        }
    }


    private void showImage(){
           showMedia(TYPE_TASK_IMAGE);
    }

    /**
     * 多媒体类型任务展示
     * @param type 媒体的类型：1 图片 2 视频 3 TTS+图片
     */
    private void showMedia(int type){
        int mediaNameId=AppApplication.getContext().getResources().getIdentifier("media_type_"+type,"string",AppApplication.getContext().getPackageName());
        String mediaName=AppApplication.getContext().getString(mediaNameId);

        String imageFilePath= DownloadUtil.getLocalFilePathByUrl(AppApplication.getContext(),this.loc.getUrl());
        if (TextUtils.isEmpty(imageFilePath)){
            getSpeaker().play(getString(R.string.pic_video_fail_tts,mediaName,SettingsDataUtil.get().getWakeupWord(getString(R.string.default_robot_name))),mTtsTaskCb);
            return;
        }

        regEvent();

        Intent intent=new Intent("com.cruzr.wayguider.picvideo");

        intent.putExtra("type",type);
        intent.putExtra("path",imageFilePath);
        intent.putExtra("loc",loc.getName());

        intent.putExtra("time",loc.getTime());

        if(type==TYPE_TASK_TTS_IMAGE){
            intent.putExtra("tts",this.loc.getContent());
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        AppApplication.getContext().startActivity(intent);
    }

    private void showVideo(){
        showMedia(TYPE_TASK_VIDEO);
    }

    private void showImageTTS(){
        showMedia(TYPE_TASK_TTS_IMAGE);
    }


    @Override
    public  void stop() {
        NLog.e(TAG,"stop()->");
        synchronized (WayGuiderPicAndVideoTaskImpl.this.mStopLock){
            WayGuiderPicAndVideoTaskImpl.this.isStop=true;
        }

        mGoTask.stop();
        mSayTask.stop();

        /**取消正在显示图片和视频的界面*/
        if(PicAndVideoUIActivity.instance!=null) {
            PicAndVideoUIActivity.instance.finish();
        }

        closeIdleCheck();

    }

    @Override
    public void timeout() {
        Log.e(TAG,"超时结束timout->"+loc.getName());

        synchronized (WayGuiderPicAndVideoTaskImpl.this.mStopLock) {
            if(WayGuiderPicAndVideoTaskImpl.this.isStop){
                WayGuiderPicAndVideoTaskImpl.this.isStop=false;
                Log.e(TAG, "timeout()1: 之前被暂停当前游览点过");
                return;
            }
        }

        //停止idle检测器
        closeIdleCheck();
        synchronized (WayGuiderPicAndVideoTaskImpl.this.mStopLock) {
            if(WayGuiderPicAndVideoTaskImpl.this.isStop){
                WayGuiderPicAndVideoTaskImpl.this.isStop=false;
                Log.e(TAG, "timeout()2: 之前被暂停当前游览点过");
                return;
            }
        }

        unRegEvent();
        finish();
        handleNextTask();

    }


    private ITtsTaskCallback mTtsTaskCb=new ITtsTaskCallback() {
        @Override
        public void onFinish(int code) {
               Log.e(TAG, loc.getName()+"->onFinish: mSayTaskCb 当前游览点所有讲完超时状态开启->start->code="+code);
               if(code== TTS_RESULT_BROKEN){
                   isTtsBroken=true;
               }

                //开始游览点空闲超时检测器超时计时
                synchronized (mLock) {
                    mIdleChecker = mIdleChecker == null ? new TaskIdleTimeoutChecker(WayGuiderPicAndVideoTaskImpl.this) : mIdleChecker;
                }
                mIdleChecker.start();

                Log.e(TAG, loc.getName()+"->onFinish: mSayTaskCb 当前游览点所有讲完超时状态开启->start");
        }
    };

    /**
     * 关闭idle检测
     */
    public void closeIdleCheck(){
        Log.e(TAG, "closeIdleCheck: "+loc.getName());
        synchronized (mLock) {
            if (mIdleChecker != null) {
                mIdleChecker.close();
                mIdleChecker = null;
            }
        }
    }

    /**
     * 判断处理下一个任务
     */
    private void handleNextTask(){
        Log.e(TAG, "handleNextTask: ->"+this.loc.getName());

        IWayGuiderTask nextTask= WayGuiderManager.getInstance(null).next();
        String nextTts;

        if(nextTask!=null){
            nextTts= getString(R.string.next_loc,nextTask.getName());
        }else {
            nextTts= getString(R.string.intro_end_normal);
            notifyNormalEnd();
        }

        getSpeaker().play(nextTts,null);

    }

    /**
     * 是否当前位置
     *
     * @param loc
     * @return true:同点 false:不同点
     */
    private boolean isCurPos(MapInfo.Location loc){

        synchronized (WayGuiderPicAndVideoTaskImpl.class) {
            Position curPos = RosRobotApi.get().getPosition(false);
            Log.e(TAG, "isCurPos: curPos.x=" + curPos.x + "->loc.getX()=" + loc.getX() + "->curPos.y=" + curPos.y + "->loc.getY()=" + loc.getY());
            return Math.abs(curPos.x - loc.getX()) <= OFF_SET && Math.abs(curPos.y - loc.getY()) <= OFF_SET;
        }
    }

    /**
     * 发消息通知整个游览过程结束状态关闭并通知状态机
     */
    private void notifyNormalEnd(){
        Event event=new Event();
        event.source=Event.EVENT_CMD_NAME_INTERNAL_END;
        NotificationCenter.defaultCenter().publish(event);
    }

    /**
     * 防止最后一个点被打断，再“继续游览”关闭问题
     */
    private void handleLastPoint(){
        Log.e(TAG, "handleLastPoint: 判断是否最后一个点被打断的继续游览");
        IWayGuiderTask nextTask= WayGuiderManager.getInstance(null).next();

        if(nextTask==null){
            String nextTts= getString(R.string.intro_end_normal);
            notifyNormalEnd();

            //延迟一会，这样"继续游览"很好衔接过来
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getSpeaker().play(nextTts,null);
        }

    }

    private Subscriber<TaskEvent> mSub=new Subscriber<TaskEvent>() {
        @Override
        public void onEvent(TaskEvent event) {
            Log.e(TAG, "taskEvent: in->"+event.in+">>loc="+loc.getName());
            switch (event.in){
                case TaskEvent.TASK_EVENT_FINISH:
                    Log.e(TAG, "taskEvent: in->event.name="+event.name+">>loc="+loc.getName());
                      if(event.name.equals(loc.getName())){
                          getSpeaker().play(getString(R.string.intro_tip_last,SettingsDataUtil.get().getWakeupWord(getString(R.string.default_robot_name))),mTtsTaskCb);
                      }
                    break;
                default:
                    break;
            }
        }
    };


}
