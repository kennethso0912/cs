package com.ubtrobot.ops.chest;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.chest.SetChestLedLightCmd;
import com.ubtrobot.ops.LedCmdOp;

/**
 * @desc : 胸灯控制
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/11
 * @modifier:
 * @modify_time:
 */

public class SetChestLedOp extends LedCmdOp {
    private final boolean enable;
    public SetChestLedOp(boolean enable) {
        super(NOR_PRIORITY);
        this.opType = TYPE_CHEST;
        this.enable = enable;
    }

    @Override
    protected int waitTime() {
        return 0;
    }

    @Override
    protected ICommand createCmd() {
        return new SetChestLedLightCmd(enable);
    }
}
