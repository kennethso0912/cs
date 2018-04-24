/*
 *
 *  *
 *  *  *
 *  *  * Copyright (c) 2008-2017 UBT Corporation.  All rights reserved.  Redistribution,
 *  *  *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 *  *  *
 *  *
 *
 */

package com.ubtrobot.ledcmds.mouth;

import android.util.Log;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.jni.LedControl;

import java.nio.ByteBuffer;

/**
 * @desc : 打开嘴巴灯
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/4/20
 * @modifier:
 * @modify_time:
 */

public class SetMouthCmd implements ICommand {
    private byte[] param;
    private int bright;
    private int onTime;
    private int offTime;
    private int runTime;
    private int mode;

    public SetMouthCmd(int bright, int onTime, int offTime, int runTime, int mode) {
        this.bright = bright;
        this.onTime = onTime;
        this.offTime = offTime;
        this.runTime = runTime;
        this.mode = mode;
    }

    public SetMouthCmd(byte[] param) {
        this.param = param;
    }

    @Override
    public boolean execute() {
        boolean ret;
        if (param != null) {
            ByteBuffer bb = ByteBuffer.wrap(param);
            bb.rewind();
            final byte bright = bb.get();
            final short onTime = bb.getShort();
            final short offTime = bb.getShort();
            final short runTime = bb.getShort();
            final int mode = bb.getInt();
            LedControl.open();
            LedControl.ledSetMouth(0,0,0,0,0);
            ret = LedControl.ledSetMouth(bright, onTime, offTime, runTime, mode);
            Log.d("led", "set mouth led: " + ret);
            LedControl.close();
        }else {
            LedControl.open();
            LedControl.ledSetMouth(0,0,0,0,0);
            ret = LedControl.ledSetMouth(bright, onTime, offTime, runTime, mode);
            Log.d("led", "set mouth led: " + ret);
            LedControl.close();
        }
        return ret;
    }
}
