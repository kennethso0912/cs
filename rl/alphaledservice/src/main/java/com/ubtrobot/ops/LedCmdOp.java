/*
 *
 *  *
 *  *  *
 *  *  * Copyright (c) 2008-2017 UBT Corporation.  All rights reserved.  Redistribution,
 *  *  *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 *  *  *
 *  *
 *
 */

package com.ubtrobot.ops;

import com.ubtrobot.ledcmds.ICommand;

/**
 * @desc : 单一串口命令操作
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/4/21
 * @modifier:
 * @modify_time:
 */
public abstract class LedCmdOp<T>  implements ICmdOp {

    protected @Priority int priority;
    protected @State int state = 0;
    protected @OpType int opType;

    public LedCmdOp(@Priority int priority){
        this.priority = priority;
    }

    @Override
    synchronized public final void prepare(){
        state = PREPARED;
    }

    @Override
    synchronized public LedOpResult start(){
        if (state == PREPARED) {
            state = RUNNING;
            ICommand cmd = createCmd();
            boolean success = cmd.execute();
            state = STOPPED;
            LedOpResult result = new LedOpResult();
            result.success = success;
            result.type = getOpType();
            return result;
        }
        return null;
    }

    @Override
    synchronized public boolean running(){
        return state == RUNNING;
    }

    @Override
    synchronized public boolean stopped(){
        return state == STOPPED;
    }

    protected abstract int waitTime();

    protected abstract ICommand createCmd();

    // FIXME: 2017/8/25
    synchronized public boolean stop(){
        return false;
    }

    @Override
    public int getOpType() {
        return opType;
    }
}
