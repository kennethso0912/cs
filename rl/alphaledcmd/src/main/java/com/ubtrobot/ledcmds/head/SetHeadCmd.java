package com.ubtrobot.ledcmds.head;

import android.util.Log;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.jni.LedControl;
import com.ubtrobot.ledentity.LedColor;

import java.nio.ByteBuffer;

/**
 * @desc : 设置头灯:支持所有参数所有可配
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/12
 * @modifier:
 * @modify_time:
 */

public class SetHeadCmd implements ICommand{
    private byte[] param;
    private int color;
    private int bright;
    private int rled;
    private int lled;
    private int on;
    private int off;
    private int total;
    private int mode;

    public SetHeadCmd(int color, int bright, int rled, int lled, int on, int off, int total, int mode) {
        this.color = color;
        this.bright = bright;
        this.rled = rled;
        this.lled = lled;
        this.on = on;
        this.off = off;
        this.total = total;
        this.mode = mode;
    }

    public SetHeadCmd(byte[] param) {
        this.param = param;
    }

    @Override
    public boolean execute() {
        boolean ret;
        if (param == null) {
            LedControl.open();
            LedControl.ledSetHead(0, 0, 0, 0, 0, 0, 0, 0);
            ret = LedControl.ledSetHead(color, bright, rled, lled, on,
                    off,
                    total,
                    mode);
            Log.d("led", "set head led: " + ret);
            LedControl.close();
        }else {
            ByteBuffer bb = ByteBuffer.wrap(param);
            bb.rewind();
            bb.limit(param.length);
            byte color = bb.get();
            byte leftLed = bb.get();
            byte rightLed = bb.get();
            byte bright = bb.get();
            short onTime = bb.getShort();
            short offTime = bb.getShort();
            short nTotalTime = bb.getShort();
            short mode = bb.getShort();
            LedControl.open();
            LedControl.ledSetHead(0, 0, 0, 0, 0, 0, 0, 0);
            ret = LedControl.ledSetHead(color, bright, rightLed, leftLed,
                    onTime,
                    offTime,
                    nTotalTime,
                    mode);
            Log.d("led", "set head led: " + ret);
            LedControl.close();
        }
        return ret;
    }
}
