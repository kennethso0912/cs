/*
 * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 */

package com.ubtechinc.cruiser.wayguide.event;

/**
 * Created on 2017/3/2.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 游览操控界面 状态回馈事件
 */

public class UIStateEvent {
	public static final int TYPE_STATE_PAUSE = 1;      //暂停游览事件
	public static final int TYPE_STATE_CONTINUE = 2;   //继续游览事件
	public static final int TYPE_STATE_END = 3;

	public static final int RESULT_FAIL= 0;            //失败
	public static final int RESULT_SUCCESS= 1;         //成功

	public  int state;
	public  int rst;
}
