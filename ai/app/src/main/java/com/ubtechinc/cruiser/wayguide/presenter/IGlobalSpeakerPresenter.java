package com.ubtechinc.cruiser.wayguide.presenter;

import com.ubtechinc.cruiser.wayguide.callback.ITtsTaskCallback;

/**
 * Created on 2017/3/1.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 全局TTS播放器 与游览讲解任务无关
 */

public interface IGlobalSpeakerPresenter {
    void speak(String tts);
    //    void speak(String tts, ITaskCallback listener);
    void speak(String tts, ITtsTaskCallback listener);
}