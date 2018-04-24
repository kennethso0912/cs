package com.ubtrobot.ledcmds.eye;

import android.util.Log;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.jni.LedControl;

import java.nio.ByteBuffer;

/**
 * @desc : 设置眼灯:支持所有参数所有可配
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/8/24
 * @modifier:
 * @modify_time:
 */

public class SetEyeCmd implements ICommand {
    private int color;
    private int bright;
    private int rled;
    private int lled;
    private int on;
    private int off;
    private int total;
    private int mode;
    private byte[] param;

    public SetEyeCmd(int color, int bright, int lled, int rled, int on, int off, int total, int mode) {
        this.color = color;
        this.bright = bright;
        this.rled = rled;
        this.lled = lled;
        this.on = on;
        this.off = off;
        this.total = total;
        this.mode = mode;
    }

    public SetEyeCmd(byte[] param) {
        this.param = param;
    }

    @Override
    public boolean execute() {
        boolean ret;
        if (param == null) {
            LedControl.open();
            LedControl.ledSetEye(0, 0, 0, 0, 0, 0, 0, 0);
            ret = LedControl.ledSetEye(color, bright, rled, lled, on, off, total, mode);
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
            short mode = bb.getShort();
            LedControl.open();
            LedControl.ledSetEye(0, 0, 0, 0, 0, 0, 0, 0);
            ret = LedControl.ledSetEye(color, bright, rightLed, leftLed, onTime, offTime, totalTime, mode);
            Log.d("led", "set head led: " + ret);
            LedControl.close();

        }
        return ret;
    }
}
