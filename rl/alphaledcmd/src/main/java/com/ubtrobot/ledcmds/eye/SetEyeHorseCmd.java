package com.ubtrobot.ledcmds.eye;

import android.util.Log;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.jni.LedControl;
import com.ubtrobot.ledentity.LedColor;

import java.nio.ByteBuffer;

/**
 * @desc : 眼睛跑马效果
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/11
 * @modifier:
 * @modify_time:
 */

public class SetEyeHorseCmd implements ICommand {
    private byte[] param;
    private int color;
    private int bright;
    private int reye;
    private int leye;
    private int on;
    private int total;

    public SetEyeHorseCmd(int color, int bright, int reye, int leye, int on, int total) {
        this.color = color;
        this.bright = bright;
        this.reye = reye;
        this.leye = leye;
        this.on = on;
        this.total = total;
    }

    public SetEyeHorseCmd(byte[] param) {
        this.param = param;
    }

    @Override
    public boolean execute() {
        boolean ret;
        if (param == null) {
            LedControl.open();
            LedControl.ledSetEye(0, 0, 0, 0, 0, 0, 0, 0);
            ret = LedControl.ledSetEye(color, bright, reye, leye, on, 0, total, 0x01);
            Log.d("led", "set eye led: " + ret);
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
            LedControl.ledSetEye(0, 0, 0, 0, 0, 0, 0, 0);
            ret = LedControl.ledSetEye(color, bright, rightLed, leftLed, onTime, offTime, totalTime, 0x01);
            LedControl.close();
        }
        return ret;
    }
}
