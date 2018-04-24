package com.ubtrobot.ops.mouth;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.mouth.SetMouthCmd;
import com.ubtrobot.ledentity.LedBright;
import com.ubtrobot.ledentity.MouthLedMode;

/**
 * @desc : 设置嘴巴灯:可配所有参数
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/5/10
 * @modifier:
 * @modify_time:
 */
public class SetMouthOp extends MouthLedOpBase {
    private byte[] param;
    private int bright;
    private int on;
    private int off;
    private int total;
    private int mode;

    public SetMouthOp(LedBright bright, int onTime, int offTime, int runTime, MouthLedMode mode) {
        super(NOR_PRIORITY);
        this.bright = bright.value;
        this.on = onTime;
        this.off = offTime;
        this.total = runTime;
        this.mode = mode.value;
    }

    public SetMouthOp(byte[] param) {
        super(NOR_PRIORITY);
        this.param = param;
    }

    @Override
    protected int waitTime() {
        return 0;
    }

    @Override
    protected ICommand createCmd() {
        if (param == null){
            return new SetMouthCmd(bright, on, off, total,mode);
        }else {
            return new SetMouthCmd(param);
        }
    }
}