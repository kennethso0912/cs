package com.ubtrobot.ledcmds.head;

import android.util.Log;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.jni.LedControl;

/**
 * @desc : 头灯常亮
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/27
 * @modifier:
 * @modify_time:
 */

public class SetHeadLightCmd implements ICommand {
    private final int bright;
    private final int color;

    public SetHeadLightCmd(int color, int bright) {
        this.color = color;
        this.bright = bright;
    }

    @Override
    public boolean execute() {
        LedControl.open();
        LedControl.ledSetHead(0,0,0,0,0,0,0,0);
        boolean ret = LedControl.ledSetHead(color,bright,0xff,0xff,Integer.MAX_VALUE, 0, Integer.MAX_VALUE, 0x07);
        Log.d("led", "set head led: " + ret);
        LedControl.close();
        return ret;
    }
}
