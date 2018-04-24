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

/**
 * @desc : 操作结果
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/4/25
 * @modifier:
 * @modify_time:
 */

public final class LedOpResult {
    public int type;
    public boolean success;

    @Override
    public String toString() {
        return "OpResult[ type =" + type + ", success="+ success + " ]";
    }
}
