package com.ubtechinc.cruiser.wayguide.presenter.impl;

import com.ubtechinc.cruiser.wayguide.callback.IDataLoadCallback;
import com.ubtechinc.cruiser.wayguide.event.Event;
import com.ubtechinc.cruiser.wayguide.interactor.IDataHandlerInteractor;
import com.ubtechinc.cruiser.wayguide.interactor.impl.DataHandlerInteractorImpl;
import com.ubtechinc.cruiser.wayguide.presenter.IDataHandlerPresenter;
import com.ubtechinc.cruiser.wayguide.presenter.base.AbstractPresenter;
import com.ubtechinc.framework.log.NLog;
import com.ubtechinc.framework.notification.NotificationCenter;
import com.ubtechinc.framework.notification.Subscriber;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des  游览数据加载业务类
 */

public final class DataHandlerPresenterImpl extends AbstractPresenter implements IDataHandlerPresenter {
    private static final String TAG="DataHandler";
    private IDataHandlerInteractor mInteractor;

    public DataHandlerPresenterImpl()
    {
        mInteractor=new DataHandlerInteractorImpl();
    }


    @Override
    public void load() {
        mInteractor.load();
    }

    @Override
    public void load(IDataLoadCallback cb) {
        mInteractor.load(cb);
    }


    @Override
    protected void registerEvent() {
        NotificationCenter.defaultCenter().subscriber(Event.class,mSubscriber);
    }

    @Override
    protected void unRegisterEvent() {
        NotificationCenter.defaultCenter().unsubscribe(Event.class,mSubscriber);
    }

    private Subscriber<Event> mSubscriber=new Subscriber<Event>() {
        @Override
        public void onEvent(Event event) {
            switch(event.in){
                case Event.EVENT_MOTION_INTRO_DATA_LOAD:
                    NLog.e(TAG,"load()->");
                    load();
                    break;
                default:
                    break;
            }
        }
    };


}
