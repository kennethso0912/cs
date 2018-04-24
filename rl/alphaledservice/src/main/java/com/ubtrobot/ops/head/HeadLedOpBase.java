package com.ubtrobot.ops.head;

import com.ubtrobot.ops.LedCmdOp;

/**
 * @desc :
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/8/22
 * @modifier:
 * @modify_time:
 */

public abstract class HeadLedOpBase extends LedCmdOp {
    public HeadLedOpBase(@Priority int priority) {
        super(priority);
        this.opType = TYPE_HEAD;
    }
}
