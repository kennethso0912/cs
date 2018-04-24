package com.ubtrobot.ops.eye;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.eye.SetEyeBlinkCmd;

/**
 * @desc : 不可配置眨眼: enable 表示开始/结束眨眼
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/6/9
 * @modifier:
 * @modify_time:
 */

public class SetEyeBlinkOp extends EyeLedOpBase {
    private final boolean blink;
    public SetEyeBlinkOp(boolean enable) {
        super(NOR_PRIORITY);
        this.blink = enable;
    }

    @Override
    protected int waitTime() {
        return 0;
    }

    @Override
    protected ICommand createCmd() {
        return new SetEyeBlinkCmd(blink);
    }
}
