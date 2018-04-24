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

package com.ubtrobot.ledcmds.eye;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.jni.LedControl;

/**
 * @desc : 眼睛灯：眨眼效果,由沈工底层实现的眨眼
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/4/20
 * @modifier:
 * @modify_time:
 */

public class SetEyeBlinkCmd implements ICommand {
    private final boolean blink;

    public SetEyeBlinkCmd(boolean enable) {
        this.blink = enable;
    }

    @Override
    public boolean execute() {
        LedControl.open();
        final boolean ret = LedControl.ledSetOn(blink? 1 : 0);
        LedControl.close();
        return ret;
    }
}
