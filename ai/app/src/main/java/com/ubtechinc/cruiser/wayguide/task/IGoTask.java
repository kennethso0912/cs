package com.ubtechinc.cruiser.wayguide.task;

import com.ubtechinc.cruiser.wayguide.callback.ITaskCallback;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 导航任务接口
 */

public interface IGoTask {
    /**
     * 开始导航到指定位置
     * @param  x
     * @param  y
     */
    void go(float x, float y, ITaskCallback cb);

    void go(float x, float y,float theta,ITaskCallback cb);

    void go(ITaskCallback cb);
    /**
     * 停止导航
     */
    void stop();
}
