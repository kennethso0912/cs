package com.ubtrobot.ledcmds.head;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.jni.LedControl;

/**
 * @desc : 关闭头灯：所有效果都关闭
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/11
 * @modifier:
 * @modify_time:
 */

public class TurnOffHeadCmd implements ICommand {

    public TurnOffHeadCmd() {}

    @Override
    public boolean execute() {
        LedControl.open();
        boolean ret = LedControl.ledSetHead(0,0,0,0,0,0,0,0);
        LedControl.close();
        return ret;
    }
}
