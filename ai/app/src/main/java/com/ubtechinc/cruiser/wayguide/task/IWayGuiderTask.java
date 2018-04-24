package com.ubtechinc.cruiser.wayguide.task;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 游览任务接口
 */

public interface IWayGuiderTask {
    /**
     * 开始整个游览任务
     */
    void start();

    /**
     * 游览是否结束
     */
    boolean isFinish();

    /**
     * 任务名称
     * @return  任务名称
     */
    String getName();

    /**
     * 停止游览任务
     */
    void stop();

    /**
     *
    IWayGuiderTask clone() throws CloneNotSupportedException;*/

    /**
     * 设置状态
     */
    void setStatus(int status);


    ISayTask getSpeaker();

    IGoTask  getWalker();

    /**
     * 再介绍一遍
     */
    void introAgain();

    /**
     * 关闭任务idle检测器
     */
    void closeIdleCheck();

}
