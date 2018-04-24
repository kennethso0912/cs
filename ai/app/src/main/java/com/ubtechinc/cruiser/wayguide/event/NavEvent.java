package com.ubtechinc.cruiser.wayguide.event;

import com.ubtechinc.cruiser.wayguide.callback.ITaskCallback;

/**
 * Created on 2017/3/2.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 导航事件
 */

public class NavEvent {
    //导航初始化
    public static final int NAV_EVENT_INIT = 1;
    //开始导航
    public static final int NAV_EVENT_START = 2;
    //停止导航
    public static final int NAV_EVENT_STOP= 3;

    public int in;
    public float x;
    public float y;
    public float theta;
    public String name;
    public ITaskCallback cb;
}
