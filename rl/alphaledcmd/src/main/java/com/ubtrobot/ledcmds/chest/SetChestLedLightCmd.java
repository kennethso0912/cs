package com.ubtrobot.ledcmds.chest;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.jni.LedControl;

/**
 * @desc :
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/12
 * @modifier:
 * @modify_time:
 */

public class SetChestLedLightCmd implements ICommand {
    private final boolean enable;

    public SetChestLedLightCmd(boolean enable) {
        this.enable = enable;
    }

    @Override
    public boolean execute() {
        LedControl.open();
        boolean ret = LedControl.ledSetOn(enable? 3 : 4);
        LedControl.close();
        return ret;
    }
}
