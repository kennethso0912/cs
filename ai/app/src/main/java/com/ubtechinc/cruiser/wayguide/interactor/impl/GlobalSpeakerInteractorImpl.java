package com.ubtechinc.cruiser.wayguide.interactor.impl;

import com.ubtechinc.cruiser.wayguide.callback.ITaskCallback;
import com.ubtechinc.cruiser.wayguide.callback.ITtsTaskCallback;
import com.ubtechinc.cruiser.wayguide.interactor.IGlobalSpeakerInteractor;
import com.ubtechinc.cruiser.wayguide.task.ISayTask;
import com.ubtechinc.cruiser.wayguide.task.impl.SayTaskImpl;

/**
 * Created on 2017/3/1.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des TTS播报模型类
 */

public class GlobalSpeakerInteractorImpl implements IGlobalSpeakerInteractor {
    private ISayTask mSpeaker;

    public GlobalSpeakerInteractorImpl(){
         mSpeaker=new SayTaskImpl();
    }

    @Override
    public void speak(String tts) {
         mSpeaker.play(tts,null);
    }

    @Override
    public void speak(String tts, ITtsTaskCallback listener) {
        mSpeaker.play(tts, listener);
    }

//    @Override
    public void speak(String tts, ITaskCallback listener) {
//         mSpeaker.play(tts, listener);
    }
}
