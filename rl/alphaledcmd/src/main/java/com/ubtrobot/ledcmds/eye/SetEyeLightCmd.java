package com.ubtrobot.ledcmds.eye;

import android.util.Log;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.jni.LedControl;

/**
 * @desc : 眼睛常量效果
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/12
 * @modifier:
 * @modify_time:
 */

public class SetEyeLightCmd implements ICommand {
    private final int color;

    public SetEyeLightCmd(int color) {
        this.color = color;
    }

    @Override
    public boolean execute() {
        LedControl.open();
        boolean ret = LedControl.ledSetOn(color);
        Log.d("led", "set eye led: " + ret);
        LedControl.close();
        return ret;
    }
}
