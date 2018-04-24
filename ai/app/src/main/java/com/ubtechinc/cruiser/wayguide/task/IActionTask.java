package com.ubtechinc.cruiser.wayguide.task;

import com.ubtechinc.cruiser.wayguide.callback.ITaskCallback;


/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des  动作任务接口
 */
public interface IActionTask {
    void play(String actName, ITaskCallback listener);
    void play();
    void stop();
}