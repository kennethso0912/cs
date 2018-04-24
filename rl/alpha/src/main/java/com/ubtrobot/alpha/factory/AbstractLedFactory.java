package com.ubtrobot.alpha.factory;

import com.ubtrobot.led.protos.LedWrapper;
import com.ubtrobot.ops.ICmdOp;
import com.ubtrobot.ops.LedCmdOp;

/**
 * Created on 2017/10/21.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public abstract class AbstractLedFactory {
     public static final int TYPE_OP_ON=1;
     public static final int TYPE_OP_OFF=0;
     public static AbstractLedFactory createFactory(int lId){
           switch (lId){
               case ICmdOp.TYPE_EYE:
                   return new LedEyeFactory();
               case ICmdOp.TYPE_HEAD:
                   return new LedHeadFactory();
               case ICmdOp.TYPE_MOUTH:
                   return new LedMouthFactory();
           }
           return null;
     }

   public  abstract LedCmdOp create(int op,LedWrapper.Led led);
}
