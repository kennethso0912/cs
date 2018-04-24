/*
 * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 */

package com.ubtechinc.cruiser.wayguide.utils;

import com.ubtechinc.cruzr.sdk.status.SystemStatus;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 游览常量类
 */

public final class UbtConstant {
	//主服务对应的APPID
	public static final int APP_ID = 123;
	//状态机对应的应用状态ID
	public static final int STATE_MACHINE_CODE= SystemStatus.STATUS_GUIDE;

	//状态机对应的应用状态ID
	public static final int CHAT_STATE_MACHINE_CODE= SystemStatus.STATUS_CHAT;

	//Bugly对应的应用APPIDd
	public static final String BUGLY_APP_ID = "6945da320a";

	//TTS正常播完结束
	public static final int TTS_RESULT_COMPLETED = 0;
	//TTS被打断结束
	public static final int TTS_RESULT_BROKEN = 1;
	//游览语音媒体TTS播放activity action
//	public  static final String INTENT_TTS="com.cruzr.wayguider.tts";//原来是一个activity tts 现在改成service tts
	public  static final String INTENT_TTS="com.cruzr.wayguider.service.tts";

    //游览图片或视频播放activity action
	public  static final String INTENT_PIC_VIDEO="com.cruzr.wayguider.picvideo";

    private UbtConstant(){}

	//获取地图所有普通点的URI
	public static final String ALL_COMMON_POINT ="content://com.ubtechinc.cruzr.map.routinepathProvider/routinepath";

    //地图未定位position:x,y.theta属性均为以下值
	public static final float LOCATION_INVALID_VALUE = 0x7f800000;



}
