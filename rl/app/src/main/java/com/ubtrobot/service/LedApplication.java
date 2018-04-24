package com.ubtrobot.service;

import android.app.Application;
import android.content.Context;

import com.ubtech.utilcode.utils.ProcessUtils;
import com.ubtrobot.master.Master;
import com.ubtrobot.ulog.logger.android.AndroidLoggerFactory;

/**
 * Created on 2017/10/10.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class LedApplication extends Application{
    private static Context mContext = null;

    public static Context getContext() {
        return mContext;
    }

    private synchronized void setContext(Context context) {
        LedApplication.mContext = context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(ProcessUtils.isMainProcess(this)) {
            setContext(this.getApplicationContext());

            //初始化，以“服务”形式集成至服务总线
            Master.initialize(this);
            // 配置总线日志工厂
            Master.get().setLoggerFactory(new AndroidLoggerFactory());
        }
    }
}
