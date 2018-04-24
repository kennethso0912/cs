package com.ubtrobot.ops.eye;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.eye.SetEyeLightCmd;
import com.ubtrobot.ops.LedCmdOp;
import com.ubtrobot.ledentity.LedColor;

/**
 * @desc : 设置眼睛灯常亮
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/12
 * @modifier:
 * @modify_time:
 */

public final class SetEyeLightOp extends LedCmdOp {
    private final int color;
    public SetEyeLightOp(LedColor color) {
        super(NOR_PRIORITY);
        //5 ~ 11
        this.color = color.value + 4;
    }

    @Override
    protected int waitTime() {
        return 0;
    }

    @Override
    protected ICommand createCmd() {
        return new SetEyeLightCmd(color);
    }
}
