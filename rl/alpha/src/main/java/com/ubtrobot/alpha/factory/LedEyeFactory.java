package com.ubtrobot.alpha.factory;

import com.ubtrobot.led.protos.LedWrapper;
import com.ubtrobot.ops.LedCmdOp;
import com.ubtrobot.ops.eye.SetEyeBlinkOp;
import com.ubtrobot.ops.eye.TurnOffEyeOp;

/**
 * Created on 2017/10/21.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class LedEyeFactory extends AbstractLedFactory {
    @Override
    public LedCmdOp create(int op, LedWrapper.Led led) {
        if(op==TYPE_OP_OFF){
            return new TurnOffEyeOp();
        }else if (op==TYPE_OP_ON){
            return new SetEyeBlinkOp(true);
        }
        return null;
    }
}
