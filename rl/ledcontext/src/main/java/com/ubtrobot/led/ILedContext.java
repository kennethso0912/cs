package com.ubtrobot.led;

import com.ubtrobot.led.protos.LedWrapper;

import java.util.List;

/**
 * Created on 2017/10/9.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public interface ILedContext {
    /**
     * 具体产品LED灯的生命周期开始的一些处理（如资源创建初始等）
     */
    public void onCreate();

    /**
     * 具体产品LED灯的生命周期结束的处理（如资源释放等）
     */
    public void onDestroy();
    /**
     * 打开LED灯
     * @param led
     */
    public void setOn(LedWrapper.Leds led);

    /**
     * 关闭LED灯
     * @param lIds 灯ID集合
     */
    public void setOff(LedWrapper.LedIds lIds);

    /**
     * 获取具体产品所具备的LED列表
     */
    public List<LedWrapper.LedInfo> getList();
}
