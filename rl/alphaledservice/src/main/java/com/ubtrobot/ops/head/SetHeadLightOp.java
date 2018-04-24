package com.ubtrobot.ops.head;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.head.SetHeadLightCmd;
import com.ubtrobot.ledentity.LedBright;
import com.ubtrobot.ledentity.LedColor;

/**
 * @desc : 头灯常亮
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/27
 * @modifier:
 * @modify_time:
 */

public class SetHeadLightOp extends HeadLedOpBase {
    private final int color;
    private final int bright;
    public SetHeadLightOp(LedColor color, LedBright bright) {
        super(NOR_PRIORITY);
        this.color = color.value;
        this.bright = bright.value;
    }

    @Override
    protected int waitTime() {
        return 0;
    }

    @Override
    protected ICommand createCmd() {
        return new SetHeadLightCmd(color, bright);
    }
}
