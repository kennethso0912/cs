package com.ubtechinc.cruiser.wayguide.task.impl;



import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantLock;

import com.ubtechinc.cruiser.wayguide.R;
import com.ubtechinc.cruiser.wayguide.app.AppApplication;
import com.ubtechinc.cruiser.wayguide.callback.ITaskCallback;
import com.ubtechinc.cruiser.wayguide.callback.ITtsTaskCallback;
import com.ubtechinc.cruiser.wayguide.event.Event;
import com.ubtechinc.cruiser.wayguide.event.TaskEvent;
import com.ubtechinc.cruiser.wayguide.map.MapNewInfo;
import com.ubtechinc.cruiser.wayguide.model.Media;
import com.ubtechinc.cruiser.wayguide.model.NavPojo;
import com.ubtechinc.cruiser.wayguide.model.TaskIdleTimeoutChecker;
import com.ubtechinc.cruiser.wayguide.model.WayGuiderManager;
import com.ubtechinc.cruiser.wayguide.task.IActionTask;
import com.ubtechinc.cruiser.wayguide.task.IGoTask;
import com.ubtechinc.cruiser.wayguide.task.IMediaTask;
import com.ubtechinc.cruiser.wayguide.task.ISayTask;
import com.ubtechinc.cruiser.wayguide.task.IWayGuiderTask;
import com.ubtechinc.cruiser.wayguide.utils.SettingsDataUtil;
import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;
import com.ubtechinc.cruzr.serverlibutil.aidl.Position;
import com.ubtechinc.framework.log.NLog;
import com.ubtechinc.framework.notification.NotificationCenter;
import com.ubtechinc.framework.notification.Subscriber;
import static com.ubtechinc.cruiser.wayguide.utils.ParserUtil.getString;
import static com.ubtechinc.cruiser.wayguide.utils.UbtConstant.TTS_RESULT_COMPLETED;


/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc  游览任务实现类
 */
public class WayGuiderMediaTaskImpl implements IWayGuiderTask, TaskIdleTimeoutChecker.IdleTimeoutCallback{
    private final static String TAG="WayGuiderTask";
//    private static final float OFF_SET = 0.5f; //之前曾设为0.7f;因为导航老有误差所以改大点
    private static final float OFF_SET = 1.5f; //之前曾设为0.7f;因为导航老有误差所以改大点
    private final static int WAY_GUIDER_SUCCESS = 1;
    private final static int WAY_GUIDER_START = 0;
    private IGoTask mGoTask;
    private IActionTask mActionTask;
    private MapNewInfo.Location loc;
    private volatile int status=WAY_GUIDER_START;
    private final ReentrantLock mLock=new ReentrantLock(true);
    private volatile TaskIdleTimeoutChecker mIdleChecker;
    private volatile boolean isStop=false;
    private final ReentrantLock mStopLock=new ReentrantLock(true);
    private IMediaTask mMediaTask;//媒体队列任务
    private volatile int mediaStatus=MEDIA_STATUS_DEFAULT;
    private ISayTask mSayTask;
    private ArrayList<Media> mGlobalMedias;

    private static final int  MEDIA_STATUS_DEFAULT= 0;//媒体初始状态
    private static final int  MEDIA_STATUS_STARTED= 1;
    private static final int  MEDIA_STATUS_FINISHED= 2;


    public WayGuiderMediaTaskImpl(MapNewInfo.Location loc)
    {
        this.loc=loc;
        this.mGoTask=new GoTaskImpl(loc.getX(),loc.getY(),loc.getTheta());
        this.mSayTask=new SayTaskImpl("");
        this.mMediaTask=new MediaTask(mSub,loc.getName());
        this.mGlobalMedias=loc.getMedias();

        //媒体队列放入全局池以便做打断记忆功能
        AppApplication.addMediasWithTaskName(loc.getName(), (ArrayList<Media>)this.mGlobalMedias.clone());

    }


    private void unRegEvent(){
        NotificationCenter.defaultCenter().unsubscribe(TaskEvent.class,mSub);
//        mSub=null;
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

        /** 以前是每个任务切片同步，不够线程安全，改为全局标识 打断true/false**/
//        synchronized (WayGuiderMediaTaskImpl.this.mStopLock){
//            WayGuiderMediaTaskImpl.this.isStop=false;
//        }


        //媒体队列放入全局池以便做打断记忆功能 偶发出现闲聊结束为空的情况（beita weizhi 出现）
//        AppApplication.addMediasWithTaskName(loc.getName(), (ArrayList<Media>)this.mGlobalMedias.clone());
        
        //判断目前是否同一个点和之前没完成所有媒体任务被打断，是，直接关闭当前游览点任务，以防止到达游览点后被暂停后再说继续游览，会重走和重说一遍
        if(isCurPos(loc)&&WayGuiderMediaTaskImpl.this.mediaStatus!=MEDIA_STATUS_DEFAULT){
              Log.e(TAG, "判断目前是否处于当前点: 是->"+loc.getName());

              //判断当前游览点是否已经完成过任务
                if(WayGuiderMediaTaskImpl.this.mediaStatus==MEDIA_STATUS_STARTED){
                    Log.e(TAG, "start: 未曾完成过当前游览点的所有媒体-->>>");

                  //由于此时是由"继续游览"进入的，延迟让讲解内容衔接继续游览播报
                  try {
                      Thread.sleep(3000);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }

                  handleLocTask();

              }else if(WayGuiderMediaTaskImpl.this.mediaStatus==MEDIA_STATUS_FINISHED) {//没被打断过讲解内容，在当前点直接跳过，不播报下一个点的继续游览
                    Log.e(TAG, "start: 当前游览点完成了所有媒体-->>>");
                  finish();
                  //最后一个点被打断后继续游览的状态关闭和播报
                  handleLastPoint();
              }

        }else {


            go(new ITaskCallback() {
                @Override
                public void onFinish() {
                    Log.e(TAG, "onFinish: 当前游览点｛" + loc.getName() + "｝导航正常结束->下面开始游览点讲解");
                    handleLocTask();

                }
            });
        }

    }

    private void go(ITaskCallback listener) {
        synchronized (WayGuiderMediaTaskImpl.class) {
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
            getSpeaker().play(getString(R.string.not_start_intro_yet), null);

            return;
        }

        synchronized (mLock) {
            //防止还未到当前点就被问及再介绍一遍而进行播报
            if (mIdleChecker == null) {
                 Log.e(TAG, "introAgain: 还未开始讲解 不能再介绍一遍->"+loc.getName());
                getSpeaker().play(getString(R.string.not_start_intro_yet),null);

                return;
            }

            if (mIdleChecker != null) {
                mIdleChecker.close();
            }
        }

        Log.e(TAG, "introAgain: start再介绍一遍开始->"+this.loc.getName());

        //媒体队列放入全局池以便做打断记忆功能
        AppApplication.addMediasWithTaskName(loc.getName(), (ArrayList<Media>)this.mGlobalMedias.clone());

        getSpeaker().play(getString(R.string.say_again), new ITtsTaskCallback() {
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
        this.mediaStatus=MEDIA_STATUS_STARTED;

        Log.e(TAG, "handleLocTask: 原始媒体列表个数："+mGlobalMedias.size()+";原始媒体列表："+mGlobalMedias);

        //游览点没有设置任何媒体任务 在此只播"这里是普通点名称"
        if(mGlobalMedias.isEmpty()){
                  Log.e(TAG, "handleLocTask: loc="+loc.getName()+";游览点没有设置任何媒体任务===>>>");
                  WayGuiderMediaTaskImpl.this.mediaStatus=MEDIA_STATUS_FINISHED;
                  String wakeupWord=SettingsDataUtil.get().getWakeupWord(getString(R.string.default_robot_name));
                  getSpeaker().play(getString(R.string.here_intro_tip_last,loc.getName(),wakeupWord ),mTtsTaskCb);
               return;
        }

//        mMediaTask.start(mGlobalMedias);

        //同步问题，出现isStop打断后无法正常初始化问题20180104 night方案一，后面改了全局的同步变量方案二
//        synchronized (WayGuiderMediaTaskImpl.this.mStopLock){
//            WayGuiderMediaTaskImpl.this.isStop=false;
//        }

        mMediaTask.start();
    }


    @Override
    public  void stop() {
        NLog.e(TAG,"stop()->");
        /** 以前是每个任务切片同步，不够线程安全，改为全局标识 打断true/false**/
//        synchronized (WayGuiderMediaTaskImpl.this.mStopLock){
//            WayGuiderMediaTaskImpl.this.isStop=true;
//        }

        mGoTask.stop();

        if(!AppApplication.isChatTTs) {
           mSayTask.stop();//与闲聊冲突暂时屏蔽
        }

        /**取消正在显示图片和视频的界面*/
        AppApplication.removeActsByTaskName(loc.getName());

        AppApplication.removeServicesByTaskName(loc.getName());//原来是activity tts 现在改成service tts

        unRegEvent();
        closeIdleCheck();

    }

    @Override
    public void timeout() {
        Log.e(TAG,"超时结束timout->"+loc.getName());
        /** 以前是每个任务切片同步，不够线程安全，改为全局标识 打断true/false**/
//        synchronized (WayGuiderMediaTaskImpl.this.mStopLock) {
//            if(WayGuiderMediaTaskImpl.this.isStop){
//                WayGuiderMediaTaskImpl.this.isStop=false;
//                Log.e(TAG, "timeout()1: 之前被暂停当前游览点过");
//                return;
//            }
//        }
        boolean isStop;
        if(isStop=WayGuiderManager.isIsStop()){
            Log.e(TAG, "timeout()1: 之前被暂停当前游览点过");
            return;
        }
        Log.e(TAG, "timeout: 1->isStop"+isStop);

        //停止idle检测器
        closeIdleCheck();
        /** 以前是每个任务切片同步，不够线程安全，改为全局标识 打断true/false**/
//        synchronized (WayGuiderMediaTaskImpl.this.mStopLock) {
//            if(WayGuiderMediaTaskImpl.this.isStop){
//                WayGuiderMediaTaskImpl.this.isStop=false;
//                Log.e(TAG, "timeout()2: 之前被暂停当前游览点过");
//                return;
//            }
//        }
        if(isStop=WayGuiderManager.isIsStop()){
            Log.e(TAG, "timeout()2: 之前被暂停当前游览点过");
            return;
        }
        Log.e(TAG, "timeout: 2->isStop"+isStop);

        unRegEvent();
        finish();
        handleNextTask();

    }


    private ITtsTaskCallback mTtsTaskCb=new ITtsTaskCallback() {
        @Override
        public void onFinish(int code) {
               Log.e(TAG, loc.getName()+"->onFinish: mSayTaskCb 当前游览点所有讲完超时状态开启->start->code="+code);

                //开始游览点空闲超时检测器超时计时
                synchronized (mLock) {
                    mIdleChecker = mIdleChecker == null ? new TaskIdleTimeoutChecker(WayGuiderMediaTaskImpl.this) : mIdleChecker;
                }
                mIdleChecker.start();
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

        ITtsTaskCallback cb;
        if(nextTask!=null){
            nextTts= getString(R.string.next_loc,nextTask.getName());
            cb=null;
        }else {
            nextTts= getString(R.string.intro_end_normal);

            cb=new ITtsTaskCallback() {
                @Override
                public void onFinish(int code) {
                    notifyNormalEnd();
                }
            };

        }
        getSpeaker().play(nextTts,cb);


    }

    /**
     * 是否当前位置
     *
     * @param loc
     * @return true:同点 false:不同点
     */
    private boolean isCurPos(MapNewInfo.Location loc){

        synchronized (WayGuiderMediaTaskImpl.class) {
            Position curPos = RosRobotApi.get().getPosition(false);
            Log.e(TAG, "isCurPos: curPos.x=" + curPos.x + "->loc.getX()=" + loc.getX() + "->curPos.y=" + curPos.y + "->loc.getY()=" + loc.getY());

            float targetX=Math.abs(curPos.x - loc.getX());
            float targetY=Math.abs(curPos.y - loc.getY());
            double distance=Math.sqrt(targetX*targetX+targetY*targetY);

            Log.e(TAG, "isCurPos: distance="+distance);
            return distance<=OFF_SET;
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
//            notifyNormalEnd();

            //延迟一会，这样"继续游览"很好衔接过来
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getSpeaker().play(nextTts, new ITtsTaskCallback() {
                @Override
                public void onFinish(int code) {
                    notifyNormalEnd();
                }
            });

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

//                          synchronized (WayGuiderMediaTaskImpl.this.mStopLock) {
//                              Log.e(TAG, "onEvent: 当前任务名:"+WayGuiderMediaTaskImpl.this.loc.getName()+">是否被打断过:"+WayGuiderMediaTaskImpl.this.isStop+">所有媒体播放完成发过来的信号");
//                              if (WayGuiderMediaTaskImpl.this.isStop) {
//                                       WayGuiderMediaTaskImpl.this.isStop=false;
//                                     return ;
//                              }
//                          }
                          /** 以前是每个任务切片同步，不够线程安全，改为全局标识 打断true/false**/
                          boolean isStop;
                          if(isStop=WayGuiderManager.isIsStop()){
                              Log.e(TAG, "onEvent: 当前任务名:"+WayGuiderMediaTaskImpl.this.loc.getName()+">是否被打断过:"+isStop+">所有媒体播放完成发过来的信号");
//                                   WayGuiderManager.setIsStop(false);
                              return ;
                          }
                          WayGuiderMediaTaskImpl.this.mediaStatus=MEDIA_STATUS_FINISHED;

//                          getSpeaker().play(getString(R.string.intro_tip_last,AppApplication.getRobotName()),mTtsTaskCb);

                          //develop-easyhome 垃圾产品没想好，做完又要改，去掉
//                          getSpeaker().play(getString(R.string.intro_tip_last),mTtsTaskCb);

                           if(mTtsTaskCb!=null){
                               mTtsTaskCb.onFinish(TTS_RESULT_COMPLETED);
                           }
                      }
                    break;
                default:
                    break;
            }
        }
    };


}
