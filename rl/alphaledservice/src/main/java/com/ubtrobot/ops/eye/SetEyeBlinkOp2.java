package com.ubtrobot.ops.eye;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.eye.SetEyeBlinkCmd2;
import com.ubtrobot.ledentity.LedBright;
import com.ubtrobot.ledentity.LedColor;

/**
 * @desc : 可配置的眨眼
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/12
 * @modifier:
 * @modify_time:
 */

public class SetEyeBlinkOp2 extends EyeLedOpBase {
    private final int color;
    private final int bright;
    private final int reye;
    private final int leye;
    private final int on;
    private final int off;
    private final int total;
    public SetEyeBlinkOp2(LedColor color, LedBright bright, int number, int on, int off, int total) {
        super(NOR_PRIORITY);
        this.color = color.value;
        this.bright = bright.value;
        this.reye = number;
        this.leye = number;
        this.on = on;
        this.off = off;
        this.total = total;
    }

    @Override
    protected int waitTime() {
        return 0;
    }

    @Override
    protected ICommand createCmd() {
        return new SetEyeBlinkCmd2(color, bright, reye, leye, on, off, total);
    }
}
