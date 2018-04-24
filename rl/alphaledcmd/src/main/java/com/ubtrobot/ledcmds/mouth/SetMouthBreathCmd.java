package com.ubtrobot.ledcmds.mouth;

import android.util.Log;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.jni.LedControl;

/**
 * @desc : 嘴巴呼吸效果
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/8/24
 * @modifier:
 * @modify_time:
 */

public final class SetMouthBreathCmd implements ICommand {
    private int offTime;
    private int onTime;
    private int runTime;

    public SetMouthBreathCmd(int onTime, int offTime, int runTime) {
        this.offTime = offTime;
        this.onTime = onTime;
        this.runTime = runTime;
    }

    @Override
    public boolean execute() {
        LedControl.open();
        LedControl.ledSetMouth(0,0,0,0,0);
        boolean ret = LedControl.ledSetMouth(0, onTime, offTime, runTime, 0x01);
        Log.d("led", "set mouth led: " + ret);
        LedControl.close();
        return ret;
    }
}
