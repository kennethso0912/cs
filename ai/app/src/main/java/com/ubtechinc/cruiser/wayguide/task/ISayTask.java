package com.ubtechinc.cruiser.wayguide.task;

import com.ubtechinc.cruiser.wayguide.callback.ITaskCallback;
import com.ubtechinc.cruiser.wayguide.callback.ITtsTaskCallback;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 讲解任务接口
 */

public interface ISayTask {
    /**
     * 开始播放游览讲解
     * @param tts
     */
//    void play(String tts, ITaskCallback cb);

    void play(String tts, ITtsTaskCallback cb);
    void play(ITaskCallback listener);

    /**
     * 停止播放游览讲解
     */
    void stop();

    void init();

}
