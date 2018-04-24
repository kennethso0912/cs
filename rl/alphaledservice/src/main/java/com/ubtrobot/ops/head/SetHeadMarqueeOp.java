package com.ubtrobot.ops.head;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.head.SetHeadHorseCmd;
import com.ubtrobot.ledentity.LedBright;
import com.ubtrobot.ledentity.LedColor;

/**
 * @desc : 设置头灯跑马
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/11
 * @modifier:
 * @modify_time:
 */

public class SetHeadMarqueeOp extends HeadLedOpBase {
    private final int color;
    private final int bright;
    private final int rled;
    private final int lled;
    private final int on;
    private final int total;

    public SetHeadMarqueeOp(LedColor color, LedBright bright, int number, int on, int total) {
        super(NOR_PRIORITY);
        this.color = color.value;
        this.bright = bright.value;
        this.rled = number;
        this.lled = number;
        this.on = on;
        this.total = total;
    }

    //双色跑马
    public SetHeadMarqueeOp(LedColor color1, LedColor color2, LedBright bright, int number, int on, int total){
        super(NOR_PRIORITY);
        this.color = (color1.value << 8)| color2.value;
        this.bright = bright.value;
        this.rled = number;
        this.lled = number;
        this.on = on;
        this.total = total;
    }

    @Override
    protected int waitTime() {
        return 0;
    }

    @Override
    protected ICommand createCmd() {
        return new SetHeadHorseCmd(color, bright, rled, lled, on, total);
    }
}
