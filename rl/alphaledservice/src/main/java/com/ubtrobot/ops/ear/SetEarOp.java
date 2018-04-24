package com.ubtrobot.ops.ear;

import com.ubtech.utilcode.utils.ConvertUtils;
import com.ubtrobot.ledcmds.ICommand;
import com.ubtrobot.ledcmds.head.SetHeadFlashCmd;
import com.ubtrobot.ops.LedCmdOp;

import java.nio.ByteBuffer;

/**
 * @desc : 设置耳朵（头部）灯：2mic上是耳朵，5mic上是头灯 //给动作文件用
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/5/2
 * @modifier:
 * @modify_time:
 */
public class SetEarOp extends LedCmdOp {
    private final byte left;
    private final byte right;
    private final byte bright;
    private final short onTime;
    private final short offTime;
    private final short totalTime;

    public SetEarOp(byte left, byte right, byte bright, short onTime, short offTime, short totalTime) {
        super(NOR_PRIORITY);
        this.opType = TYPE_EAR;
        this.left = left;
        this.right = right;
        this.bright = bright;
        this.onTime = onTime;
        this.offTime = offTime;
        this.totalTime = totalTime;
    }

    @Override
    protected int waitTime() {
        return 0;
    }

    @Override
    protected ICommand createCmd() {
        ByteBuffer bb = ByteBuffer.allocate(9);
        bb.rewind();
        bb.limit(9);
        bb.put(left);
        bb.put(right);
        bb.put(bright);
        bb.put(ConvertUtils.h_short2Byte(onTime));
        bb.put(ConvertUtils.h_short2Byte(offTime));
        bb.put(ConvertUtils.h_short2Byte(totalTime));
        bb.rewind();
        byte[] bytes = new byte[9];
        bb.get(bytes);
        // FIXME: 2017/8/24 logic 没有耳朵灯接口
        return new SetHeadFlashCmd(bytes);
    }
}