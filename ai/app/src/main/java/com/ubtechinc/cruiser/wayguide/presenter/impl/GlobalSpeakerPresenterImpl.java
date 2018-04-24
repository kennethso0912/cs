package com.ubtechinc.cruiser.wayguide.presenter.impl;

import com.ubtechinc.cruiser.wayguide.callback.ITtsTaskCallback;
import com.ubtechinc.cruiser.wayguide.event.TtsEvent;
import com.ubtechinc.cruiser.wayguide.interactor.IGlobalSpeakerInteractor;
import com.ubtechinc.cruiser.wayguide.interactor.impl.GlobalSpeakerInteractorImpl;
import com.ubtechinc.cruiser.wayguide.presenter.IGlobalSpeakerPresenter;
import com.ubtechinc.cruiser.wayguide.presenter.base.AbstractPresenter;
import com.ubtechinc.framework.notification.NotificationCenter;
import com.ubtechinc.framework.notification.Subscriber;

/**
 * Created on 2017/3/1.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des TTS业务类
 */

public final class GlobalSpeakerPresenterImpl extends AbstractPresenter implements IGlobalSpeakerPresenter {
    private IGlobalSpeakerInteractor mInteractor;

    public GlobalSpeakerPresenterImpl(){
        this.mInteractor=new GlobalSpeakerInteractorImpl();
    }

    @Override
    public void speak(String tts) {
        this.mInteractor.speak(tts);
    }

    @Override
    public void speak(String tts, ITtsTaskCallback listener) {
         this.mInteractor.speak(tts, listener);
    }

    @Override
    protected void registerEvent() {
        NotificationCenter.defaultCenter().subscriber(TtsEvent.class,mSubcriber);
    }

    @Override
    protected void unRegisterEvent() {
        NotificationCenter.defaultCenter().unsubscribe(TtsEvent.class,mSubcriber);
    }

    private Subscriber<TtsEvent> mSubcriber=new Subscriber<TtsEvent>() {
        @Override
        public void onEvent(TtsEvent event) {
            if(event.in== TtsEvent.EVENT_MOTION_INTRO_SPEAK){
                speak(event.source);
            }
        }
    };
}
