package com.ubtrobot.ops.eye;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.eye.SetEyeFlashCmd;
import com.ubtrobot.ledentity.LedBright;
import com.ubtrobot.ledentity.LedColor;

/**
 * @desc : 眼睛闪烁
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/11
 * @modifier:
 * @modify_time:
 */

public class SetEyeFlashOp extends EyeLedOpBase {
    private byte[] param;
    private int color;
    private int bright;
    private int reye;
    private int leye;
    private int on;
    private int total;

    public SetEyeFlashOp(LedColor color, LedBright bright, int number, int on, int total) {
        super(NOR_PRIORITY);
        this.color = color.value;
        this.bright = bright.value;
        this.reye = number;
        this.leye = number;
        this.on = on;
        this.total = total;
    }

    public SetEyeFlashOp(byte[] param) {
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
            return new SetEyeFlashCmd(color, bright, reye, leye, on, total);
        }else {
            return new SetEyeFlashCmd(param);
        }
    }
}
