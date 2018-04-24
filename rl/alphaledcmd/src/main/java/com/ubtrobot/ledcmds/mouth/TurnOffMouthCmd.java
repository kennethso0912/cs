package com.ubtrobot.ledcmds.mouth;

import android.util.Log;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.jni.LedControl;

/**
 * @desc : 关闭嘴巴灯
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/8/24
 * @modifier:
 * @modify_time:
 */

public class TurnOffMouthCmd implements ICommand {
    public TurnOffMouthCmd() {}

    @Override
    public boolean execute() {
        LedControl.open();
        boolean ret = LedControl.ledSetMouth(0,0,0,0,0);
        Log.d("led", "set mouth led: " + ret);
        LedControl.close();
        return ret;
    }
}
