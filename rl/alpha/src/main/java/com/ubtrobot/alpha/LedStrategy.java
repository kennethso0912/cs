package com.ubtrobot.alpha;

import com.ubtrobot.alpha.factory.AbstractLedFactory;
import com.ubtrobot.led.protos.LedWrapper;
import com.ubtrobot.ops.ICmdOp;
import com.ubtrobot.ops.LedCmdOp;
import com.ubtrobot.ops.LedOpController;
import com.ubtrobot.ops.LedOpResultListener;

/**
 * Created on 2017/10/21.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class LedStrategy {
    private LedOpController controller;
    private ICmdOp op;
    private AbstractLedFactory factory;

    private LedStrategy(){
        controller=LedOpController.instance();
    }

    private static final class Holder {
        private static LedStrategy _instance = new LedStrategy();
    }

    public static LedStrategy get(){
         return Holder._instance;
    }

    public void excute(int opType, LedWrapper.Led led, LedOpResultListener listener){
        factory=AbstractLedFactory.createFactory(led.getLId());
        LedCmdOp cmdOp= factory.create(opType,led);
        controller.executeOp(cmdOp,listener);
    }

    public void destroy(){
        controller.destroy();
    }

}
