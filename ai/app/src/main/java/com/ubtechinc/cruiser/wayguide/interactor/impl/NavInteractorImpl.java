package com.ubtechinc.cruiser.wayguide.interactor.impl;


import android.util.Log;

import com.ubtechinc.cruiser.wayguide.R;
import com.ubtechinc.cruiser.wayguide.callback.ITaskCallback;
import com.ubtechinc.cruiser.wayguide.callback.ITtsTaskCallback;
import com.ubtechinc.cruiser.wayguide.event.Event;
import com.ubtechinc.cruiser.wayguide.interactor.INavInteractor;
import com.ubtechinc.cruiser.wayguide.model.NavPojo;
import com.ubtechinc.cruiser.wayguide.presenter.impl.WayGuiderPresenterImpl;
import com.ubtechinc.cruiser.wayguide.service.ServiceProxy;
import com.ubtechinc.cruiser.wayguide.task.ISayTask;
import com.ubtechinc.cruiser.wayguide.task.impl.SayTaskImpl;
import com.ubtechinc.cruzr.sdk.ros.RosConstant;
import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;
import com.ubtechinc.cruzr.serverlibutil.aidl.Position;
import com.ubtechinc.cruzr.serverlibutil.interfaces.RemoteCommonListener;
import com.ubtechinc.framework.notification.NotificationCenter;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import static com.ubtechinc.cruiser.wayguide.service.ServiceProxy.SERVICE_WAY_GUIDER;
import static com.ubtechinc.cruiser.wayguide.utils.ParserUtil.*;
import static com.ubtechinc.cruiser.wayguide.utils.UbtConstant.TTS_RESULT_BROKEN;
import static com.ubtechinc.cruiser.wayguide.utils.UbtConstant.TTS_RESULT_COMPLETED;

/**
 * Created on 2017/3/2.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des  导航功能模型类
 */

public class NavInteractorImpl implements INavInteractor {
    private static final String TAG = "KS_Nav";
    private volatile WeakHashMap<NavPojo,ITaskCallback> mPool=new WeakHashMap<>();
    private static final int MAX_SPEED = 10;
//    private static final float OFF_SET = 0.5f;因为导航老有误差所以改大
//    private static final float OFF_SET = 1.0f;
    private static final float OFF_SET = 1.5f;
    private volatile boolean isStart=false;
    private static final float NAV_FAIL_OFF_SET = 1.5f;
    private static final String NAV_ERROR_NO_LOCATE = "locate_failed";
    private static final String NAV_ERROR_UNREACHABLE_DESTINATION = "unreachable_destination";

    private static final float PI=3.1415926f;


    public NavInteractorImpl(){
        Collections.synchronizedMap(mPool);
    }
    @Override
    public void onCreate() {}

    @Override
    public void init() {
        Log.e(TAG, "init: register Nav->");
        RosRobotApi.get().registerCommonCallback(mNavRemoteComCb);
    }

    @Override
    public void start(float x, float y, String name, ITaskCallback cb) {
          start(x,y,0,name,cb);
    }

    @Override
    public void start(float x, float y, float theta, String name, ITaskCallback cb) {
        isStart=false;
        NavPojo nav=new NavPojo();
        nav.setX(x);
        nav.setY(y);
        nav.setName(name);
        nav.setTheta(theta);

        if(cb!=null) {
            isStart=true;
            regNavCb(nav, cb);
        }
        Log.e(TAG, "before->start: x:"+x+"->y:"+y);

        //防止出现结束后，并发重复出现的问题
        WayGuiderPresenterImpl wpl= (WayGuiderPresenterImpl) ServiceProxy.getPresenter(SERVICE_WAY_GUIDER);
        if(!wpl.isStarted()&&cb!=null){
            return;
        }
        Log.e(TAG, "really->start: x:"+x+"->y:"+y);

        RosRobotApi.get().navigateToByPresetedSpeed(x,y,theta);
    }

    @Override
    public void stop() {
         Log.e(TAG, "stop: nav");
         isStart=false;
         RosRobotApi.get().cancelNavigate();

    }

    /**
     * 注册游览点对应用导航结果回调监听
     *
     * @param nav
     * @param cb
     */
    private void regNavCb(NavPojo nav,ITaskCallback cb){
        mPool.put(nav, cb);
    }



    private RemoteCommonListener mNavRemoteComCb=new RemoteCommonListener() {
        @Override
        public void onResult(int sessionId, int status, String msg) {
            Log.e("NCB", "onResult: sessionId:"+sessionId+"->status:"+status+"->msg:"+msg);
//            if (NAV_ERROR_NO_LOCATE.equals(msg)||NAV_ERROR_UNREACHABLE_DESTINATION.equals(msg)){  slam导航模块不可达到问题，暂时屏蔽，待ROS去处理
            if (NAV_ERROR_NO_LOCATE.equals(msg)){
                speak(getString(msg));
                notifyNavFailEnd();
                return;
            }

            if(status== RosConstant.Action.ACTION_FINISHED){

                travelNavPool(OFF_SET,getString(R.string.nav_is_incorrent),status);

            }else if(status == RosConstant.Action.ACTION_FAILED){  //导航失败

                travelNavPool(NAV_FAIL_OFF_SET,getString(R.string.nav_fail),status);

            }
        }
    };

    /**
     *查找游览导航目标点
     * @param offSet 与目标点的偏移值
     * @param msg 失败信息
     */
    private void travelNavPool(float offSet,String msg,int status) {
        Position curPos = RosRobotApi.get().getPosition(false);
        float x=curPos.x,y=curPos.y;

        Iterator<Map.Entry<NavPojo,ITaskCallback>> iterator=mPool.entrySet().iterator();
        Map.Entry<NavPojo,ITaskCallback> entry;
        NavPojo nav;

        while (iterator.hasNext()){
            entry = iterator.next();
            nav = entry.getKey();

            Log.e(TAG, "onResult: nav.getX():"+nav.getX()+"->x:"+x+"->nav.getY():"+nav.getY()+"->y:"+y);
            if(isSamePoint(nav,x,y,offSet)){
                   Log.e(TAG, "onResult: isSamePoint->nav.getX():"+nav.getX()+"->x:"+x+"->nav.getY():"+nav.getY()+"->y:"+y+"_isStart:"+isStart);
                   if(isStart) {
                       isStart=false;

                       //如果障碍物失败在附近距离之内到达成功之外还要增加角度的转动
                       if(status==RosConstant.Action.ACTION_FAILED){
                           RosRobotApi.get().moveTo(0,0,nav.getTheta(),PI/3);
                       }

                       entry.getValue().onFinish();
                   }
                   iterator.remove();//
                 return;
            }

        }
        //以下由于目标偏移值到达不到，播报导航不准确出来别老不知道啥问题
        if(isStart) {
             isStart=false;
             speak(msg);
             //类似cancel事件,导航失败后只能说“开始游览”
             notifyNavFailEnd();
        }
    }

    /**
     * 判断导航成功返回结果是否与游览位置同一个点(判断方法：到达实际坐标是否在以目标坐标为圆心偏移值为半径的圆内)
     *
     * @param     nav abc
     * @param     x
     * @param     y
     * @return  true:同一点 false:不同点
     */
    private boolean isSamePoint(NavPojo nav,float x,float y,float targetOffSet){
//        return Math.abs(nav.getX()-x)<=OFF_SET && Math.abs(nav.getY()-y)<=OFF_SET;
          float targetX=Math.abs(nav.getX()-x);
          float targetY=Math.abs(nav.getY()-y);
          double distance=Math.sqrt(targetX*targetX+targetY*targetY);
          Log.e(TAG, "isSamePoint: distance="+distance);
//        return distance<=Math.sqrt(targetOffSet);
        return distance<=targetOffSet;
    }



    /**
     * 导航失败TTS回调
     */
    private ITtsTaskCallback mTtsCb=new ITtsTaskCallback() {
        @Override
        public void onFinish(int code) {
             if(code== TTS_RESULT_BROKEN){
                 Log.e(TAG, "onFinish: 游览导航失败播报提示被打断后重播");
             }else if(code==TTS_RESULT_COMPLETED){
                 Log.e(TAG, "onFinish: 游览导航失败播报提示成功播完");
             }

        }
    };

    /**
     * 导航失败的游览过程的状态关闭
     */
    private void notifyNavFailEnd(){
        notifyNavFail(Event.EVENT_MOTION_INTRO_NAV_FAIL_END,Event.EVENT_CMD_NAME_END_SILENCE);
    }


    private void speak(String tts){
        ISayTask speaker=new SayTaskImpl();
        speaker.play(tts,mTtsCb);
    }

    private void notifyNavFail(int in,String source){
        Event event=new Event();
        event.in=in;
        event.source=source;
        NotificationCenter.defaultCenter().publish(event);
    }


}
