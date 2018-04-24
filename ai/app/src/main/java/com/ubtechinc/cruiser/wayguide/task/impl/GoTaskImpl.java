package com.ubtechinc.cruiser.wayguide.task.impl;


import com.ubtechinc.cruiser.wayguide.callback.ITaskCallback;
import com.ubtechinc.cruiser.wayguide.event.NavEvent;
import com.ubtechinc.cruiser.wayguide.task.IGoTask;
import com.ubtechinc.framework.notification.NotificationCenter;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des  导航任务实现类
 */

public class GoTaskImpl implements IGoTask {
    private float x;
    private float y;
    private float theta;

    public GoTaskImpl(){}

    public GoTaskImpl(float x, float y){
        this.x=x;
        this.y=y;
    }
    public GoTaskImpl(float x, float y,float theta){
        this.x=x;
        this.y=y;
        this.theta=theta;
    }
    
    @Override
    public void go(float x, float y, ITaskCallback cb) {
        notifyNav(NavEvent.NAV_EVENT_START,x,y,cb);
    }

    @Override
    public void go(float x, float y, float theta, ITaskCallback cb) {
        notifyNav(NavEvent.NAV_EVENT_START,x,y,theta,cb);
    }

    @Override
    public void go(ITaskCallback listener) {
//         go(this.x,this.y,listener);
        go(this.x,this.y,this.theta,listener);
    }

    @Override
    public void stop() {
         notifyNav(NavEvent.NAV_EVENT_STOP,0,0,null);
    }


    private void notifyNav(int in,float x,float y,ITaskCallback cb){
        NavEvent event=new NavEvent();
        event.in=in;
        event.cb=cb;
        event.x=x;
        event.y=y;
        NotificationCenter.defaultCenter().publish(event);
    }

    private void notifyNav(int in,float x,float y,float theta,ITaskCallback cb){
        NavEvent event=new NavEvent();
        event.in=in;
        event.cb=cb;
        event.x=x;
        event.y=y;
        event.theta=theta;
        NotificationCenter.defaultCenter().publish(event);
    }
}
