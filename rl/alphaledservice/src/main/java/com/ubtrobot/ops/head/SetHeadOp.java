package com.ubtrobot.ops.head;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.head.SetHeadCmd;
import com.ubtrobot.ledentity.HeadLedMode;
import com.ubtrobot.ledentity.LedBright;
import com.ubtrobot.ledentity.LedColor;

/**
 * @desc : 设置头灯:所有参数可配
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/11
 * @modifier:
 * @modify_time:
 */

public class SetHeadOp extends HeadLedOpBase {
    private int color;
    private int bright;
    private int lled;
    private int rled;
    private int on;
    private int off;
    private int total;
    private int mode;
    private byte[] param;

    public SetHeadOp(LedColor color, LedBright bright, int lled, int rled, int on, int off, int total, HeadLedMode mode) {
        super(NOR_PRIORITY);
        this.color = color.value;
        this.bright = bright.value;
        this.lled = lled;
        this.rled = rled;
        this.on = on;
        this.off = off;
        this.total = total;
        this.mode = mode.value;
    }

    public SetHeadOp(byte[] param) {
        super(NOR_PRIORITY);
        this.param = param;
    }

    @Override
    protected int waitTime() {
        return 0;
    }

    @Override
    protected ICommand createCmd() {
        if (param == null) {
            return new SetHeadCmd(color, bright, rled, lled, on, off, total, mode);
        }else {
            return new SetHeadCmd(param);
        }
    }
}
