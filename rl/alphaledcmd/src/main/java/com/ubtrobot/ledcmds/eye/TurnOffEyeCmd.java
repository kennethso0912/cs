package com.ubtrobot.ledcmds.eye;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.jni.LedControl;

/**
 * @desc :关掉眼睛灯：所有效果都将关闭
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/8/21
 * @modifier:
 * @modify_time:
 */

public final class TurnOffEyeCmd implements ICommand {

    public TurnOffEyeCmd() {}

    @Override
    public boolean execute() {
        LedControl.open();
        boolean ret =LedControl.ledSetEye(0,0,0,0,0,0,0,0);
        LedControl.close();
        return ret;
    }
}
