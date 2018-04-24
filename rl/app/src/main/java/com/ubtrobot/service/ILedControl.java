package com.ubtrobot.service;

import com.ubtrobot.led.protos.LedWrapper;

import java.util.List;

/**
 * Created on 2017/10/9.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public interface ILedControl {
    public void init();
    public void onDestroy();
    public void setOn(LedWrapper.Leds led);
    public void setOff(LedWrapper.LedIds lIds);
    public List<LedWrapper.LedInfo> getList();
}
