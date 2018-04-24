package com.ubtechinc.alphamini;

/**
 * @desc : 嘴巴灯控控制接口
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/13
 */

public interface ILedController {
    /**
     * 打开led灯
     */
    void turnOn();

    /**
     * 关闭led灯
     */
    void turnOff();

    /**
     *
     * @param color 颜色值
     * @param duration 持续时长 -1表示无限
     */
    void startNormalModel(int color, int duration);

    /**
     *
     * param color 颜色值
     * @param breathDuration 呼吸一次时长
     * @param duration 呼吸总时长 -1表示无限
     */
    void startBreathModel(int color, int breathDuration, int duration);


    /**
     *
     * @param color 颜色值
     * @param duration 呼吸总时长
     */
    void startBreathModel(int color, int duration);
}
