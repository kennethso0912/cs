package com.ubtrobot.ops.eye;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.eye.TurnOffEyeCmd;

/**
 * @desc : 关闭眼睛灯效果
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/11
 * @modifier:
 * @modify_time:
 */

public class TurnOffEyeOp extends EyeLedOpBase {

    public TurnOffEyeOp() {
        super(NOR_PRIORITY);
    }

    @Override
    protected int waitTime() {
        return 0;
    }

    @Override
    protected ICommand createCmd() {
        return new TurnOffEyeCmd();
    }
}
