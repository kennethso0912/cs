package com.ubtechinc.cruiser.wayguide.model;

import com.ubtechinc.cruiser.wayguide.task.IWayGuiderTask;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 游览管理接口
 */
public interface IWayGuiderManager {
    /**
     * 开启游览
     */
    void start();

    /**
     * 暂停游览
     */
    void pause();

    /**
     * 结束游览
     */
    void end();

    /**
     * 开始下一个游览点
     */
    IWayGuiderTask next();

    /**
     * 被暂停后的重新继续游览
     */
    void reset();

    /**
     * 再介绍一遍
     */
    void introAgain();

    void returnParkPoint();
}
