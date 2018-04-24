package com.ubtrobot.ops.mouth;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.mouth.TurnOffMouthCmd;

/**
 * @desc : 关闭嘴巴灯
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/8/24
 * @modifier:
 * @modify_time:
 */

public final class TurnOffMouthOp extends MouthLedOpBase {

    public TurnOffMouthOp() {
        super(NOR_PRIORITY);
    }

    @Override
    protected int waitTime() {
        return 0;
    }

    @Override
    protected ICommand createCmd() {
        return new TurnOffMouthCmd();
    }
}
