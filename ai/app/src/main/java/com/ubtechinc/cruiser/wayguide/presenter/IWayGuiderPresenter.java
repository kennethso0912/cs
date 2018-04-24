package com.ubtechinc.cruiser.wayguide.presenter;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des
 */

public interface IWayGuiderPresenter {
    /**
     * 开始游览
     */
    void start();

    /**
     * 暂停游览
     */
    void pause();

    /**
     * 继续游览
     */
    void reset();

    /**
     * 结束浏览
     */
    void end();

    /**
     * 下个地点游览
     */
    void next();
}