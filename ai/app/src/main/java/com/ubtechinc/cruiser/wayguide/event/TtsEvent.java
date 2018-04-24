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
 * @Des 事件总线基类、用于解耦操作
 */

public class TtsEvent {
	public static final int  EVENT_MOTION_INTRO_SPEAK= 7;

	public int in=-1;//事件类型
	public String source;//事件内容
}
