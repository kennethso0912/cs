package com.ubtechinc.alphamini;

import android.util.Log;

import com.ubtech.utilcode.utils.StringUtils;
import com.ubtechinc.alphamini.factory.CmdOp;
import com.ubtechinc.alphamini.factory.LedFactory;
import com.ubtrobot.led.ILedContext;
import com.ubtrobot.led.protos.LedWrapper;

import java.util.List;

/**
 * @desc : Led灯实现类
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/13
 */

public class LedContextImpl implements ILedContext{

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void setOn(LedWrapper.Leds led) {
        List<LedWrapper.Led> ledList = led.getLedsList();
        for(LedWrapper.Led led1 : ledList) {
            onSetOn(led1);
        }
    }

    private void onSetOn(LedWrapper.Led led1) {
        ILedController ledController = LedFactory.createFactory(led1.getLId());
        String duration = led1.getEffectMap().get(CmdOp.KEY_DURATION);
        if(led1.getEffectCount() != 0) {
            String breathDuration = led1.getEffectMap().get(CmdOp.KEY_BREATH_DURATION);
            if(!StringUtils.isEmpty(breathDuration)) {
                int iBreathDuration = Integer.valueOf(breathDuration);
                int breathSubDuration = iBreathDuration / 4;
                try {
                    ledController.startBreathModel(led1.getColor(), breathSubDuration, Integer.valueOf(duration));
                    return ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        int iDuration = -1;
        if(duration != null && !duration.equals("")) {
            try {
                iDuration = Integer.valueOf(duration);
            } catch (Exception e) {

            }
        }
        ledController.startNormalModel(led1.getColor(), iDuration);
    }

    @Override
    public void setOff(LedWrapper.LedIds lIds) {
        List<Integer> idsList=lIds.getIdsList();
        LedWrapper.Led led=null;
        for(int ids : idsList) {
            onSetOff(ids);
        }
    }

    private void onSetOff(int ids) {
        ILedController ledController = LedFactory.createFactory(ids);
        ledController.turnOff();
    }

    @Override
    public List<LedWrapper.LedInfo> getList() {
        return null;
    }
}
