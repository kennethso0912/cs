package com.ubtechinc.cruiser.wayguide.presenter;

import com.ubtechinc.cruiser.wayguide.callback.ITaskCallback;

/**
 * Created on 2017/3/2.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 导航操作接口(导航的结果回调和停止是统一)
 */

public interface INavPresenter {
    /**
     * 初始化导航，注册导航状态回调监听
     */
    void init();

    /**
     * 开始导航到指定的坐标
     * @param x
     * @param y
     * @param name
     * @param cb
     */
    void start(float x, float y, String name, ITaskCallback cb);

    void start(float x, float y, float theta,String name, ITaskCallback cb);

    /**
     * 停止导航
     */
    void stop();
}
