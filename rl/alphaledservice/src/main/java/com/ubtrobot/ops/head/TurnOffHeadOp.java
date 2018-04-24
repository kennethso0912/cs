package com.ubtrobot.ops.head;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.head.TurnOffHeadCmd;

/**
 * @desc : 关闭头灯
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/11
 * @modifier:
 * @modify_time:
 */

public class TurnOffHeadOp extends HeadLedOpBase {

    public TurnOffHeadOp() {
        super(NOR_PRIORITY);
    }

    @Override
    protected int waitTime() {
        return 0;
    }

    @Override
    protected ICommand createCmd() {
        return new TurnOffHeadCmd();
    }
}
