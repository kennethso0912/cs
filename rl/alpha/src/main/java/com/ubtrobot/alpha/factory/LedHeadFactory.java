package com.ubtrobot.alpha.factory;

import com.ubtrobot.led.protos.LedWrapper;
import com.ubtrobot.ledentity.LedBright;
import com.ubtrobot.ledentity.LedColor;
import com.ubtrobot.ops.LedCmdOp;
import com.ubtrobot.ops.head.SetHeadLightOp;
import com.ubtrobot.ops.head.TurnOffHeadOp;

/**
 * Created on 2017/10/21.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class LedHeadFactory extends AbstractLedFactory {
    @Override
    public LedCmdOp create(int op, LedWrapper.Led led) {
        if(op==TYPE_OP_OFF){
            return new TurnOffHeadOp();
        }else if (op==TYPE_OP_ON){
            return new SetHeadLightOp(LedColor.valueOf(led.getColor()), LedBright.valueOf(led.getBright()));
        }
        return null;
    }
}
