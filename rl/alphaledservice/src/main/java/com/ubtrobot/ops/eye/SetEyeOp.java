package com.ubtrobot.ops.eye;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.eye.SetEyeBlinkCmd;
import com.ubtrobot.ledcmds.eye.SetEyeCmd;
import com.ubtrobot.ledcmds.eye.SetEyeLightCmd;
import com.ubtrobot.ledentity.EyeLedMode;
import com.ubtrobot.ledentity.LedBright;
import com.ubtrobot.ledentity.LedColor;

/**
 * @desc : 眼睛灯设置:可设置所有参数
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/5/2
 * @modifier:
 * @modify_time:
 */
public class SetEyeOp extends EyeLedOpBase {
    private byte[] param;
    private int color;
    private int bright;
    private int lled;
    private int rled;
    private int on;
    private int off;
    private int total;
    private int mode;

    public SetEyeOp(LedColor color, LedBright bright, int lled, int rled, int on, int off, int total, EyeLedMode mode) {
        super(NOR_PRIORITY);
        this.lled = lled;
        this.rled = rled;
        this.bright = bright.value;
        this.color = color.value;
        this.on = on;
        this.off = off;
        this.total = total;
        this.mode = mode.value;
    }
    
    public SetEyeOp(byte[] param){
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
            if (mode == EyeLedMode.COMMON.value
                    || mode == EyeLedMode.MARQUEE.value
                    || mode == EyeLedMode.FLASH.value) {
                return new SetEyeCmd(color, bright, rled, lled, on, off, total, mode);
            }else if (mode == EyeLedMode.BLINK.value){
                return new SetEyeBlinkCmd(true);
            }else {
                return new SetEyeLightCmd(color + 4);
            }
        }else {
            return new SetEyeCmd(param);
        }
    }
}