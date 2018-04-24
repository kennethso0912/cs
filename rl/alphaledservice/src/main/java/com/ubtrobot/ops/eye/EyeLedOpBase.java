package com.ubtrobot.ops.eye;

import com.ubtrobot.ops.LedCmdOp;

/**
 * @desc :
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/8/22
 * @modifier:
 * @modify_time:
 */

public abstract class EyeLedOpBase extends LedCmdOp {
    public EyeLedOpBase(@Priority int priority) {
        super(priority);
        this.opType = TYPE_EYE;
    }
}
