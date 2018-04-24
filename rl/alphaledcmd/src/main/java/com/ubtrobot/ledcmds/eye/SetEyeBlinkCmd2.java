package com.ubtrobot.ledcmds.eye;

import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.jni.LedControl;

/**
 * @desc : 眨眼效果：使用普通模式模拟眨眼，效果没有：@link{com.ubtechinc.alpha.cmds.eye.SetEyeBlinkCmd}好
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/7/12
 * @modifier:
 * @modify_time:
 */

public class SetEyeBlinkCmd2 implements ICommand {
    private final int color;
    private final int bright;
    private final int reye;
    private final int leye;
    private final int on;
    private final int off;
    private final int total;

    public SetEyeBlinkCmd2(int color, int bright, int reye, int leye, int on, int off, int total) {
        this.color = color;
        this.bright = bright;
        this.reye = reye;
        this.leye = leye;
        this.on = on;
        this.off = off;
        this.total = total;
    }

    @Override
    public boolean execute() {
        LedControl.open();
        LedControl.ledSetEye(0,0,0,0,0,0,0,0);
        boolean ret = LedControl.ledSetEye(color,bright,reye,leye ,on, off, total, 0);
        LedControl.close();
        return ret;
    }
}
