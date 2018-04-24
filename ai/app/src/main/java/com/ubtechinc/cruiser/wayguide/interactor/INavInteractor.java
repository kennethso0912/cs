package com.ubtechinc.cruiser.wayguide.interactor;

import com.ubtechinc.cruiser.wayguide.callback.ITaskCallback;

/**
 * Created on 2017/3/2.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des
 */

public interface INavInteractor {
    void onCreate();
    //导航前初始化
    void init();
    //开始导航
    void start(float x, float y, String name, ITaskCallback cb);

    //开始导航
    void start(float x, float y,float theta,String name, ITaskCallback cb);
    //停止导航
    void stop();

}
