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

public class Event {
	/** ----------------- 游览指令事件 ------------------------**/
	//再讲解一遍
	public static final int EVENT_MOTION_INTRO_AGAIN = 0;
	//开始游览
	public static final int EVENT_MOTION_INTRO_START = 1;
	//继续游览
	public static final int EVENT_MOTION_INTRO_CONTINUE = 2;
	//暂停游览
	public static final int EVENT_MOTION_INTRO_PAUSE = 3;
	//结束游览
	public static final int EVENT_MOTION_INTRO_END = 4;
	//正常结束游览,各游览状态恢复初始化
	public static final int EVENT_MOTION_INTRO_NORMAL_END = 8;
	//导航失败结束游览
	public static final int EVENT_MOTION_INTRO_NAV_FAIL_END = 9;

	//映射业务类的方法名
	public static final String EVENT_CMD_NAME_START = "invokeStart";
	public static final String EVENT_CMD_NAME_PAUSE = "invokePause";
	public static final String EVENT_CMD_NAME_CONTINUE = "invokeContinue";
	public static final String EVENT_CMD_NAME_CONTINUE_SILENCE= "invokeContinueSilence";
	public static final String EVENT_CMD_NAME_END = "invokeEnd";
	public static final String EVENT_CMD_NAME_INTRO_AGAIN = "invokeIntroAgain";
	public static final String EVENT_CMD_NAME_END_SILENCE = "invokeEndSilence";
	public static final String EVENT_CMD_NAME_INTERNAL_END= "invokeInternalEnd";
	public static final String EVENT_CMD_NAME_VIEW_DOWNLOAD= "viewdownload";

	//真正结束游览后关闭游览状态
	public static final String EVENT_CMD_NAME_REALLY_END = "invokeLastReallyEnd";

	/** ----------------- 游览数据事件   ------------------------**/
	//游览数据加载
	public static final int EVENT_MOTION_INTRO_DATA_LOAD = 5;


    /**------------------   游览事件内容属性          -----------------------**/
	public int in=-1;//事件类型
	public String source;//事件内容
}
