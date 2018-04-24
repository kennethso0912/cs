package com.ubtechinc.cruiser.wayguide.ui.base;

import android.app.Activity;
import android.os.Bundle;

import com.ubtechinc.cruiser.wayguide.task.IActionTask;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created on 2017/11/6.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public abstract class BaseMediaActivity extends Activity{
    //显示延迟时间
    private final static long delayTime=10*1000;
    private IActionTask mActionTask;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 处理当前媒体
     */
    protected abstract  void handleCurMedia();

    /**
     * 处理下一媒体
     */
    protected abstract void handleNextMedia();

    private void close(){
        mActionTask.stop();

        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                handleCurMediaEnd();
            }
        };

        mTimer.schedule(task,delayTime*1000);
    }

    /**
     * 处理当前媒体结束
     */
    protected abstract void handleCurMediaEnd();


}
