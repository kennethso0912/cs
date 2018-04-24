package com.ubtrobot.ledcmds.head;

import android.util.Log;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.jni.LedControl;

import java.nio.ByteBuffer;

/**
 * @desc : 头灯呼吸效果
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/11
 * @modifier:
 * @modify_time:
 */

public class SetHeadBreathCmd implements ICommand {
    private byte[] param;
    private int color;
    private int bright;
    private int rled;
    private int lled;
    private int on;
    private int total;
    public SetHeadBreathCmd(int color, int bright, int rled, int lled, int on, int total) {
        this.color = color;
        this.bright = bright;
        this.rled = rled;
        this.lled = lled;
        this.on = on;
        this.total = total;
    }

    public SetHeadBreathCmd(byte[] param) {
        this.param = param;
    }

    @Override
    public boolean execute() {
        boolean ret;
        if (param == null) {
            LedControl.open();
            LedControl.ledSetHead(0, 0, 0, 0, 0, 0, 0, 0);
            //呼吸效果，on的单位是200ms
            ret = LedControl.ledSetHead(color, bright, rled, lled, (on/200==0?1:on/200), 0, total, 0x01);
            Log.d("led", "set head led: " + ret);
            LedControl.close();
        }else {
            ByteBuffer bb = ByteBuffer.wrap(param);
            bb.rewind();
            bb.limit(param.length);
            byte leftLed = bb.get();
            byte rightLed = bb.get();
            byte bright = bb.get();
            byte color = bb.get();
            short onTime = bb.getShort();
            short offTime = bb.getShort();
            short totalTime = bb.getShort();
            LedControl.open();
            LedControl.ledSetHead(0, 0, 0, 0, 0, 0, 0, 0);
            ret = LedControl.ledSetHead(color, bright, rightLed, leftLed, onTime, offTime, totalTime, 0x01);
            Log.d("led", "set head led: " + ret);
            LedControl.close();
        }
        return ret;
    }
}
