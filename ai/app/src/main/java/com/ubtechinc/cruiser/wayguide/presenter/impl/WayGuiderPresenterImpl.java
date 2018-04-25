package com.ubtechinc.cruiser.wayguide.presenter.impl;

import android.content.Intent;
import android.util.Log;
import com.ubtechinc.cruiser.wayguide.R;
import com.ubtechinc.cruiser.wayguide.app.AppApplication;
import com.ubtechinc.cruiser.wayguide.callback.IDataLoadCallback;
import com.ubtechinc.cruiser.wayguide.callback.ITaskCallback;
import com.ubtechinc.cruiser.wayguide.event.Event;
import com.ubtechinc.cruiser.wayguide.event.NavEvent;
import com.ubtechinc.cruiser.wayguide.event.TtsEvent;
import com.ubtechinc.cruiser.wayguide.event.UIStateEvent;
import com.ubtechinc.cruiser.wayguide.interactor.IUserValidateInteractor;
import com.ubtechinc.cruiser.wayguide.interactor.IWayGuiderInteractor;
import com.ubtechinc.cruiser.wayguide.interactor.impl.UserValidateInteractorImpl;
import com.ubtechinc.cruiser.wayguide.interactor.impl.WayGuiderInteractorImpl;
import com.ubtechinc.cruiser.wayguide.model.WayGuiderManager;
import com.ubtechinc.cruiser.wayguide.presenter.IDataHandlerPresenter;
import com.ubtechinc.cruiser.wayguide.presenter.IWayGuiderPresenter;
import com.ubtechinc.cruiser.wayguide.presenter.base.AbstractPresenter;
import com.ubtechinc.cruiser.wayguide.service.ServiceProxy;
import com.ubtechinc.cruiser.wayguide.utils.SettingsDataUtil;
import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;
import com.ubtechinc.cruzr.sdk.speech.SpeechRobotApi;
import com.ubtechinc.cruzr.sdk.status.SystemStatus;
import com.ubtechinc.cruzr.sdk.status.SystemStatusApi;
import com.ubtechinc.cruzr.serverlibutil.aidl.Position;
import com.ubtechinc.cruzr.serverlibutil.interfaces.RemoteStatusListener;
import com.ubtechinc.cruzr.serverlibutil.interfaces.RemoteWarnListener;
import com.ubtechinc.framework.notification.NotificationCenter;
import com.ubtechinc.framework.notification.Subscriber;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static com.ubtechinc.cruiser.wayguide.utils.ParserUtil.*;
import static com.ubtechinc.cruiser.wayguide.utils.UbtConstant.*;


/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc  游览业务类（游览主要相关业务的通讯逻辑）
 */
public final class WayGuiderPresenterImpl  extends AbstractPresenter implements IWayGuiderPresenter{
    private static final String TAG = "WayGuiderPre";
    private static final String ACTION_WG_CONTROL_UI = "com.cruzr.wayguider.UI";
    private static final int EMERGENCY_STOP_KEY = 0x7fffffff;
    private static final int EMERGENCY_STOP_VALUE =0;

    private IWayGuiderInteractor mWGInteractor;
    private IUserValidateInteractor mUserInteractor;
    private volatile boolean isStarted;
    private volatile boolean isPause;
    //从享元池取出数据处理业务类
    private IDataHandlerPresenter mDataPresenter= (IDataHandlerPresenter)ServiceProxy.getPresenter(ServiceProxy.SERVICE_DATA);

    public WayGuiderPresenterImpl()
    {
        mWGInteractor=new WayGuiderInteractorImpl();
        mUserInteractor= new UserValidateInteractorImpl();
        Log.e(TAG, "observeWGPauseStatus: --->>>");
        observeWGPauseStatus();
        observeEmergencyStop();
    }

    @Override
    public void start() {
        Log.e(TAG, "start: ");
        //拦截底盘无用
        boolean enableUnderPan= SettingsDataUtil.get().getUnderPanCanUsable();
        if(!enableUnderPan){
            speak(getString(R.string.underpan_can_not_use));
            return;
        }

        boolean isNotLocated=isNotLocated();
        if(isNotLocated){
            speak(getString(R.string.locate_failed));
            return;
        }

        mDataPresenter.load(new IDataLoadCallback() {
                @Override
                public void onFinish() {//游览数据加载成功
                    Log.e(TAG, "WayGuider datas had been loaded successfully->");
                    speak(getString(R.string.follow_me));

                    startControlUI();

                    isStarted=true;

                    //通知状态机游览开始
                    SystemStatusApi.get().setAppStatus(STATE_MACHINE_CODE,true);

//                    speak(getString(R.string.follow_me));
                    mWGInteractor.start();

                }

                @Override
                public void onFail(String err) {//游览数据加载失败
                    Log.e(TAG, "onFail: err->"+err);

                    isStarted=false;
                    speak(err);
                }
        });

    }


    private boolean isNotLocated(){
        Position curPos = RosRobotApi.get().getPosition(false);
        return (curPos.x==LOCATION_INVALID_VALUE&&curPos.y==LOCATION_INVALID_VALUE&&curPos.theta==LOCATION_INVALID_VALUE);
    }

    /**
     * 开始游览后，调起游览控制界面
     */
    private void startControlUI() {
        Intent intent =new Intent(ACTION_WG_CONTROL_UI);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppApplication.getContext().startActivity(intent);
    }

    private void speak(String tts) {
        TtsEvent event=new TtsEvent();
        event.in=TtsEvent.EVENT_MOTION_INTRO_SPEAK;
        event.source= tts;
        NotificationCenter.defaultCenter().publish(event);
    }

    @Override
    public void pause() {
        if(!WayGuiderManager.isParking()) {
            mWGInteractor.pause();
        }else {//正在parking
//            RosRobotApi.get().cancelNavigate();//直接cancel不能保证和NavInteractor Nav 回调的结果的状态一致，所以统一用NavInteractor来停止导航
            notifyNav(NavEvent.NAV_EVENT_STOP,0,0,null);
        }
    }

    @Override
    public void end() {
        AppApplication.clearAllUI();

        AppApplication.clearAllService();//原来是activity tts 现在改成service tts

        if(!WayGuiderManager.isParking()) {
            mWGInteractor.end();
        }else {
            notifyNav(NavEvent.NAV_EVENT_STOP,0,0,null);
            WayGuiderManager.setIsParking(false);
        }
    }

    @Override
    public void reset() {
        if(!WayGuiderManager.isParking()) {
            mWGInteractor.reset();
        }else {
            returnParkPlace();
        }
    }

    @Override
    protected void registerEvent() {
        NotificationCenter.defaultCenter().subscriber(Event.class,mSubscriber);
    }

    @Override
    protected void unRegisterEvent() {
        NotificationCenter.defaultCenter().unsubscribe(Event.class,mSubscriber);
    }

    @Override
    public void next() {
        mWGInteractor.next();
    }

    private static final String CMD_SILENCE_TAG = "Silence";


    private long mContinueStartTime=System.currentTimeMillis();
    private long mPauseStartTime=System.currentTimeMillis();


    private Subscriber<Event> mSubscriber =new Subscriber<Event>() {
        @Override
        public void onEvent(Event event) {
            String methodName=event.source;
            Log.e(TAG, "onEvent: cmd_name="+methodName);

            //用户权限验证
            if(!mUserInteractor.validate()&&!methodName.contains(CMD_SILENCE_TAG)) {
                Log.e(TAG, "onEvent: 未开启管理员模式");
                if(event.in<=Event.EVENT_MOTION_INTRO_END){
                    speak(getString(R.string.no_admin));
                }
                return;
            }
            //以上计划替换为拦截器模式


            if (timeIntervalControl(methodName)) {
                return;
            }

            Log.e(TAG, "really->onEvent: cmd_name="+methodName);

            try {
                Method method=WayGuiderPresenterImpl.class.getMethod(methodName,new Class[0]);
                method.invoke(WayGuiderPresenterImpl.this,new Object[0]);
            }catch (NoSuchMethodException e){

            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    };

    private boolean timeIntervalControl(String methodName) {
        if(methodName.equals(Event.EVENT_CMD_NAME_START)){
            mContinueStartTime=System.currentTimeMillis();
        }else if(methodName.equals(Event.EVENT_CMD_NAME_PAUSE)){
            //时间间隔控制，类似按钮的重复多次点击
            if(System.currentTimeMillis()-mContinueStartTime<=5*1000) {
                return true;
            }
            mPauseStartTime=System.currentTimeMillis();
        }else if(methodName.equals(Event.EVENT_CMD_NAME_CONTINUE)||methodName.equals(Event.EVENT_CMD_NAME_CONTINUE_SILENCE)){
            if(System.currentTimeMillis()-mPauseStartTime<0.8*1000){
                Log.e(TAG, "继续游览事件: 暂停时间太短->WAY_GUIDE_CONTINUE");
                return true;
            }
            mContinueStartTime=System.currentTimeMillis();
        }
        return false;
    }

    public void invokeEndSilence() {
        if(isStarted) {
            initWGStatus();
            end();
        }else {
            Log.e(TAG, "onEvent: 导航当中:未开始游览");
        }
    }

    public void invokeIntroAgain() {
        if(isStarted) {
            introAgain();
        }else {
            speak(getString(R.string.not_start));
        }
    }

    public void invokeContinue(){
        continueWG(false);
    }

    public void invokeContinueSilence(){
        continueWG(true);
    }

    public void continueWG(boolean silence) {
        if(isStarted&&isPause) {
            notifyUIState(UIStateEvent.TYPE_STATE_CONTINUE,UIStateEvent.RESULT_SUCCESS);

            isPause=false;

            if(!silence) {
                speak(getString(R.string.continue_way_guider));
            }
            reset();
        }else if(!isStarted){
            notifyUIState(UIStateEvent.TYPE_STATE_CONTINUE,UIStateEvent.RESULT_FAIL);
            speak(getString(R.string.not_start));
        } else if(!isPause){
            notifyUIState(UIStateEvent.TYPE_STATE_CONTINUE,UIStateEvent.RESULT_FAIL);
            speak(getString(R.string.not_pause));
        }
    }

    public void invokeEnd() {
        if(isStarted) {
            notifyUIState(UIStateEvent.TYPE_STATE_END,UIStateEvent.RESULT_SUCCESS);

            initWGStatus();
            end();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            speak(getString(R.string.intro_end_answer));
        }else {
            notifyUIState(UIStateEvent.TYPE_STATE_END,UIStateEvent.RESULT_FAIL);
            speak(getString(R.string.not_start));
        }
    }

    public void invokePause() {
        if(isStarted&&!isPause) {
            notifyUIState(UIStateEvent.TYPE_STATE_PAUSE,UIStateEvent.RESULT_SUCCESS);

            isPause=true;
            pause();
        }else if(!isStarted){
            notifyUIState(UIStateEvent.TYPE_STATE_PAUSE,UIStateEvent.RESULT_FAIL);
            Log.e(TAG, "onEvent: 暂停指令当中：未开始游览");
        }else if(isPause) {
            notifyUIState(UIStateEvent.TYPE_STATE_PAUSE,UIStateEvent.RESULT_FAIL);
            Log.e(TAG, "onEvent: 游览暂停中->");

            if(isPauseByChat){
                isPauseByChat=false;
            }
        }
    }

    /**
     *
     */
    public void invokeInternalEnd(){
        /**
        initWGStatus();
        AppApplication.clearAllUI();

        AppApplication.clearAllService();//原来是activity tts 现在改成service tts
         */

        returnParkPlace();
    }

    /**
     * 最终真实结束关闭游览的相关的状态
     */
    public void invokeLastReallyEnd(){
        initWGStatus();
        AppApplication.clearAllUI();

        AppApplication.clearAllService();//原来是activity tts 现在改成service tts

    }

    private void returnParkPlace(){
        mWGInteractor.returnParkPoint();
    }


    /**
     * 调用游览开始指令
     */
    public void invokeStart(){
        Log.i(TAG, "onEvent: ->开始执行游览队列任务");
        if(!isStarted) {
            start();
        }else{
            Log.e(TAG, "onEvent: isStart->已开始过->");
            speak(getString(R.string.had_started));
        }
    }

    /**
     * 重置游览所有初始状态
     */
    private void initWGStatus(){
        Log.e(TAG, "initWGStatus: ->");
        isStarted=false;
        isPause=false;

        //通知状态机游览结束
        SystemStatusApi.get().removeAppStatus(STATE_MACHINE_CODE);
    }
    private void introAgain(){
         mWGInteractor.introAgain();
    }

    private volatile boolean isPauseByChat=false;
    /**
     * 监听游览暂停且无其它活跃状态
     */
    private void observeWGPauseStatus(){
          SystemStatusApi.get().registerStatusCallback(new RemoteStatusListener() {
              @Override
              public void onResult(List<Integer> list, boolean change, int state) {
                 /**当前游览中，闲聊状态切入进来，暂停游览**/
//                  Log.e(TAG, "onResult: list="+list+";change="+change+";state="+state);
                if (isGuidingEnterChat(list, change, state)) {
                    Log.e(TAG, "onResult: 监听状态机->当前游览ing->闲聊in:暂停游览");

                    AppApplication.isChatTTs=true;

                    isPauseByChat=true;

                    publishEvent(Event.EVENT_MOTION_INTRO_PAUSE,Event.EVENT_CMD_NAME_PAUSE);
                    return;
                }

                /**当前游览中，闲聊状态切入进来，暂停游览，闲聊中，然后闲聊正常结束，又恢复继续游览**/
                if(isGuidingExistChat(list, change, state)&&isPauseByChat){
                    Log.e(TAG, "onResult: 监听状态机->当前游览pausing->闲聊end:继续游览");

                    AppApplication.isChatTTs=false;

                    isPauseByChat=false;

                    publishEvent(Event.EVENT_MOTION_INTRO_CONTINUE,Event.EVENT_CMD_NAME_CONTINUE_SILENCE);
                    return;
                }
              }

              @Override
              public void onStatusPause(int paused,int pausedBy,String sponsor){//当前游览被(PC,Charge)打断才会回调此方法
                    Log.e(TAG, "onStatusPause: paused="+paused+";pausedBy="+pausedBy);
                    if(!isInterruptedByElecSkin(paused,pausedBy)) {
                        notifyEndWayGuiderSilence();
                    }
              }

              @Override
              public void onStatusFree(int var1){

              }

          });

    }

    private  boolean isInterruptedByElecSkin(int paused,int pauseBy){
        return (paused==SystemStatus.STATUS_GUIDE&&pauseBy==SystemStatus.STATUS_ELEC_SKIN);
    }

    private void notifyEndWayGuiderSilence(){
        Event event=new Event();
        event.source=Event.EVENT_CMD_NAME_END_SILENCE;
        NotificationCenter.defaultCenter().publish(event);
    }

    private boolean isGuidingEnterChat(List<Integer> list, boolean change, int state) {
        return list.size()==2&&list.contains(STATE_MACHINE_CODE)&&change&&state==CHAT_STATE_MACHINE_CODE&&isStarted;
    }

    private boolean isGuidingExistChat(List<Integer> list, boolean change, int state) {
        return list.size()==1&&list.contains(STATE_MACHINE_CODE)&&!change&&state==CHAT_STATE_MACHINE_CODE&&isPause;
    }


    /**
     * 监听急停按钮被按下
     */
    private void observeEmergencyStop(){
        RosRobotApi.get().registerWarnCallback(new RemoteWarnListener() {
            @Override
            public void onResult(String s, int key, int values, int data) {
//                Log.e(TAG, "onResult: key="+key+";value="+values);
                //value 0:normal !0:emergency
                if(key==EMERGENCY_STOP_KEY&&values!=EMERGENCY_STOP_VALUE&&isStarted){
                    Log.e(TAG, "onResult: receive the emergency signal by warn interface->");
                     //send pause wayguide msg
                    notifyWayGuiderPause();
                }
            }
        });
    }


    private void notifyWayGuiderPause(){
        publishEvent(Event.EVENT_MOTION_INTRO_PAUSE,Event.EVENT_CMD_NAME_PAUSE);
    }



    /**
     * 给游览操控界面发送状态操作结果
     * @param state
     * @param rst
     */
    private void notifyUIState(int state,int rst){
        UIStateEvent event=new UIStateEvent();
        event.state=state;
        event.rst=rst;
        NotificationCenter.defaultCenter().publish(event);
    }


    private void publishEvent(int eventType,String cmd) {
        Event event=new Event();
        event.in=eventType;
        event.source=cmd;
        NotificationCenter.defaultCenter().publish(event);
    }

   public boolean isStarted(){
        return isStarted;
   }

    private void notifyNav(int in,float x,float y,ITaskCallback cb){
        NavEvent event=new NavEvent();
        event.in=in;
        event.cb=cb;
        event.x=x;
        event.y=y;
        NotificationCenter.defaultCenter().publish(event);
    }

}
