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

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @desc :
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/4/24
 * @modifier:
 * @modify_time:
 */

public interface ICmdOp<T> {

    public static final int PREPARED = 1;
    public static final int RUNNING = 2;
    public static final int STOPPED = 3;

    @IntDef(value = {PREPARED, RUNNING, STOPPED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State{}

    public static final int MAX_PRIORITY = 0;
    public static final int NOR_PRIORITY = 1;
    public static final int MIN_PRIORITY = 2;

    @IntDef(value = {MAX_PRIORITY, NOR_PRIORITY, MIN_PRIORITY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Priority{}

    static final int TYPE_HEAD = 0;// 头灯
    static final int TYPE_EYE = 1;//眼灯
    static final int TYPE_MOUTH = 2;//嘴灯
    static final int TYPE_EAR = 3;//耳灯
    static final int TYPE_CHEST = 4;//胸灯

    @IntDef(value = {TYPE_HEAD, TYPE_EYE, TYPE_MOUTH, TYPE_EAR,TYPE_CHEST})
    @Retention(RetentionPolicy.SOURCE)
    @interface OpType{}

    void prepare();

    LedOpResult start();

    boolean stop();

    boolean running();

    boolean stopped();

    @OpType
    int getOpType();
}
