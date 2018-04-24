package com.ubtrobot.ops.mouth;

import com.ubtrobot.ops.LedCmdOp;

/**
 * @desc :
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/8/24
 * @modifier:
 * @modify_time:
 */

public abstract class MouthLedOpBase extends LedCmdOp {
    public MouthLedOpBase(@Priority int priority) {
        super(priority);
        opType = TYPE_MOUTH;
    }
}
