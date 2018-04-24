package com.ubtrobot.service;

import android.content.Context;


import com.ubtrobot.led.ILedContext;
import com.ubtrobot.led.protos.LedWrapper;

import java.util.List;

/**
 * Created on 2017/10/9.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class LedControlImpl implements ILedControl{
    private ILedContext ledContext;

    private LedControlImpl(){
        Context context=LedApplication.getContext();
        ledContext=new LedInjector(context).provideSpeechApi();
//        ledContext=new LedContextImpl();
    }
    private static final class Holder {
        private static LedControlImpl _api = new LedControlImpl();
    }
    public static LedControlImpl get(){
        return Holder._api;
    }
    @Override
    public void init() {
          ledContext.onCreate();
    }

    @Override
    public void onDestroy() {
          ledContext.onDestroy();
    }

    @Override
    public void setOn(LedWrapper.Leds leds) {
          ledContext.setOn(leds);
    }

    @Override
    public void setOff(LedWrapper.LedIds lIds) {
          ledContext.setOff(lIds);
    }

    @Override
    public List<LedWrapper.LedInfo> getList() {
          return ledContext.getList();
    }
}
