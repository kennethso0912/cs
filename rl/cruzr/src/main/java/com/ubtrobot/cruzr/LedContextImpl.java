package com.ubtrobot.cruzr;

import android.util.Log;

import com.ubtrobot.led.ILedContext;
import com.ubtrobot.led.protos.LedWrapper;

import java.util.List;

/**
 * Created on 2017/10/18.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class LedContextImpl implements ILedContext{
    private static final String TAG = "cruzr";

    @Override
    public void onCreate() {
          //初始资源
    }

    @Override
    public void onDestroy() {
          //销毁资源
    }

    @Override
    public void setOn(LedWrapper.Leds leds) {
          List<LedWrapper.Led> ledList=leds.getLedsList();
        for (LedWrapper.Led led:ledList) {
            Log.e(TAG, "setOn: led->id:"+led.getLId()+";color:"+led.getColor()+";bright:"+led.getBright()+";effect"+led.getEffectMap());
        }
    }

    @Override
    public void setOff(LedWrapper.LedIds lIds) {
         List<Integer> idsList=lIds.getIdsList();
         for (Integer id:idsList){
             Log.e(TAG, "setOff: id:"+id);
         }
    }

    @Override
    public List<LedWrapper.LedInfo> getList() {
        return null;
    }
}
