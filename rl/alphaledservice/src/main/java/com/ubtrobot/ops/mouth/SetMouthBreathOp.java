package com.ubtrobot.ops.mouth;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.mouth.SetMouthBreathCmd;

/**
 * @desc : 嘴巴呼吸效果
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/8/24
 * @modifier:
 * @modify_time:
 */

public final class SetMouthBreathOp extends MouthLedOpBase {
    private int on;
    private int off;
    private int total;

    public SetMouthBreathOp(int on, int off, int total) {
        super(NOR_PRIORITY);
        this.on = on;
        this.off = off;
        this.total = total;
    }

    public SetMouthBreathOp() {
        super(NOR_PRIORITY);
    }

    @Override
    protected int waitTime() {
        return 0;
    }

    @Override
    protected ICommand createCmd() {
        return new SetMouthBreathCmd(on, off, total);
    }
}
