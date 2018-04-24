package com.ubtechinc.cruiser.wayguide.task.impl;

import android.util.Log;

import com.ubtechinc.cruiser.wayguide.callback.ITaskCallback;
import com.ubtechinc.cruiser.wayguide.callback.ITtsTaskCallback;
import com.ubtechinc.cruiser.wayguide.task.ISayTask;
import com.ubtechinc.cruiser.wayguide.utils.UbtConstant;
import com.ubtechinc.cruzr.sdk.speech.SpeechRobotApi;
import com.ubtechinc.cruzr.serverlibutil.interfaces.SpeechTtsListener;

import static com.ubtechinc.cruiser.wayguide.utils.UbtConstant.*;


/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 讲解任务实现类
 */

public class SayTaskImpl implements ISayTask {
    private static final String TAG = "Say";
    private volatile  boolean isPause;
    private volatile boolean isSpeaking;
    private String tts;

    public SayTaskImpl(){}

    SayTaskImpl(String tts){
        this.isSpeaking=false;
        this.isPause=false;
        this.tts=tts;
    }
//    @Override
    public void play(String tts, final ITaskCallback listener) {
            isSpeaking = true;
            Log.e(TAG, "isPause=" + isPause + "say: start->tts" + tts);

            if (isPause) {
                return;
            }

            //调用主服务的TTS播报接口
//            SpeechRobotApi.get().speechStartTTS(tts, new SpeechTtsListener() {
//                @Override
//                public void onEnd() {
//                    if (listener!=null){
//                        listener.onFinish();
//                    }
//                }
//            });

    }

    @Override
    public void play(final String tts, final ITtsTaskCallback cb) {
        isSpeaking = true;
        Log.e(TAG, "isPause=" + isPause + "say: start->tts" + tts);

        if (isPause) {
            return;
        }
        SpeechRobotApi.get().speechStartTTS(tts, new SpeechTtsListener() {
            @Override
            public void onAbort() {
                Log.e(TAG, "TTS->onAbort: ->tts="+tts);
                if(cb!=null){
                    Log.e(TAG, "onAbort: cb!=null->tts"+tts);
                    cb.onFinish(TTS_RESULT_BROKEN);
                }
            }

            @Override
            public void onEnd() {
                Log.e(TAG, "TTS->onEnd: ->tts="+tts);
                if(cb!=null){
                    Log.e(TAG, "onEnd: cb!=null->tts="+tts);
                    cb.onFinish(TTS_RESULT_COMPLETED);
                }
            }
        });

    }

    @Override
    public void play(ITaskCallback listener) {
        play(this.tts,listener);
    }

    @Override
    public void stop() {
            if (!isSpeaking) {
                   return;
            }
            isPause = true;

            SpeechRobotApi.get().speechStopTTS();
    }

    @Override
    public void init() {
            isPause = false;
            isSpeaking = false;
    }

}
