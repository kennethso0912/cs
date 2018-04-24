package com.ubtrobot.alpha.factory;

import com.ubtrobot.led.protos.LedWrapper;
import com.ubtrobot.ledentity.LedBright;
import com.ubtrobot.ledentity.MouthLedMode;
import com.ubtrobot.ops.LedCmdOp;
import com.ubtrobot.ops.eye.TurnOffEyeOp;
import com.ubtrobot.ops.mouth.SetMouthOp;
import com.ubtrobot.ops.mouth.TurnOffMouthOp;

import java.util.Map;

/**
 * Created on 2017/10/21.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class LedMouthFactory extends AbstractLedFactory {
    @Override
    public LedCmdOp create(int op, LedWrapper.Led led) {
        if(op==TYPE_OP_OFF){
            return new TurnOffMouthOp();
        }else if (op==TYPE_OP_ON){
            Map<String,String> map= led.getEffectMap();

            LedBright bright=LedBright.valueOf(led.getBright());
            int onTime=Integer.parseInt(map.get("onTime"));
            int offTime=Integer.parseInt(map.get("offTime"));
            int totalTime=Integer.parseInt(map.get("totalTime"));
            MouthLedMode mode=MouthLedMode.valueOf(Integer.parseInt(map.get("mode")));

            return new SetMouthOp(bright,onTime,offTime,totalTime,mode);
        }
        return null;
    }
}
