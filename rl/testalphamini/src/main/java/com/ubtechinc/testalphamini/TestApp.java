package com.ubtechinc.testalphamini;

import android.app.Application;
import com.ubtrobot.master.Master;
import com.ubtrobot.ulog.logger.android.AndroidLoggerFactory;

/**
 * @desc :
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/14
 */
public class TestApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Master.initialize(this);
        Master.get().setLoggerFactory(new AndroidLoggerFactory());
    }
}
