package com.ubtrobot.ops.eye;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.eye.SetEyeHorseCmd;
import com.ubtrobot.ledentity.LedBright;
import com.ubtrobot.ledentity.LedColor;

/**
 * @desc : 设置眼睛跑马模式
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/11
 * @modifier:
 * @modify_time:
 */

public class SetEyeMarqueeOp extends EyeLedOpBase {
    private final int color;
    private final int bright;
    private final int reye;
    private final int leye;
    private final int on;
    private final int total;

    public SetEyeMarqueeOp(LedColor color, LedBright bright, int number, int on, int total) {
        super(NOR_PRIORITY);
        this.color = color.value;
        this.bright = bright.value;
        this.reye = number;
        this.leye = number;
        this.on = on;
        this.total = total;
    }

    public SetEyeMarqueeOp(LedColor color1, LedColor color2, LedBright bright, int number, int on, int total) {
        super(NOR_PRIORITY);
        this.color = (color1.value << 8) | (color2.value);
        this.bright = bright.value;
        this.reye = number;
        this.leye = number;
        this.on = on;
        this.total = total;
    }

    @Override
    protected int waitTime() {
        return 0;
    }

    @Override
    protected ICommand createCmd() {
        return new SetEyeHorseCmd(color, bright, reye, leye, on, total);
    }

}
