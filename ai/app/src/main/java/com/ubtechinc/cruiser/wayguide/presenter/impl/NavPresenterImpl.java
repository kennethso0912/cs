package com.ubtechinc.cruiser.wayguide.presenter.impl;

import android.util.Log;
import com.ubtechinc.cruiser.wayguide.callback.ITaskCallback;
import com.ubtechinc.cruiser.wayguide.event.NavEvent;
import com.ubtechinc.cruiser.wayguide.interactor.INavInteractor;
import com.ubtechinc.cruiser.wayguide.interactor.impl.NavInteractorImpl;
import com.ubtechinc.cruiser.wayguide.presenter.INavPresenter;
import com.ubtechinc.cruiser.wayguide.presenter.base.AbstractPresenter;
import com.ubtechinc.framework.notification.NotificationCenter;
import com.ubtechinc.framework.notification.Subscriber;

/**
 * Created on 2017/3/2.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 导航业务类
 */

public final class NavPresenterImpl extends AbstractPresenter implements INavPresenter {
    private static final String TAG = "NavPre";
    private INavInteractor mInteractor;

    public NavPresenterImpl(){}

    @Override
    public void onCreate() {
        super.onCreate();
        mInteractor=new NavInteractorImpl();
        init();
    }

    @Override
    public void init() {
        mInteractor.init();
    }

    @Override
    public void start(float x, float y, String name, ITaskCallback cb) {
        mInteractor.start(x, y, name, cb);
    }

    @Override
    public void start(float x, float y, float theta, String name, ITaskCallback cb) {
        mInteractor.start(x, y,theta,name, cb);
    }

    @Override
    public void stop() {
        mInteractor.stop();
    }

    @Override
    protected void registerEvent() {
        NotificationCenter.defaultCenter().subscriber(NavEvent.class,mSub);
    }

    @Override
    protected void unRegisterEvent() {
        NotificationCenter.defaultCenter().unsubscribe(NavEvent.class,mSub);
    }

    private Subscriber<NavEvent> mSub=new Subscriber<NavEvent>() {
        @Override
        public void onEvent(NavEvent navEvent) {
            Log.e(TAG, "onEvent: in->"+navEvent.in);
             switch (navEvent.in){
                 case NavEvent.NAV_EVENT_INIT:
                       init();
                     break;
                 case NavEvent.NAV_EVENT_START:
//                       start(navEvent.x,navEvent.y,navEvent.name,navEvent.cb);
                       start(navEvent.x,navEvent.y,navEvent.theta,navEvent.name,navEvent.cb);
                     break;
                 case NavEvent.NAV_EVENT_STOP:
                       stop();
                     break;
                 default:
                     break;
             }
        }
    };


}
