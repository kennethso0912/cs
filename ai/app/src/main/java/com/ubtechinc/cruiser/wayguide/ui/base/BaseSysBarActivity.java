package com.ubtechinc.cruiser.wayguide.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;

import com.ubtechinc.cruzr.sdk.face.CruzrFaceApi;

/**
 * Created on 2017/11/4.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public abstract class BaseSysBarActivity extends Activity{
    private static final String ACTION_SYSTEM_BAR_HIDE = "com.ubt.cruzr.hidebar";
    private static final String ACTION_SYSTEM_BAR_SHOW = "com.ubt.cruzr.showbar";
    private static final int TIME_HIDE_BAR_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        forbiddenFace();

        onCreateSuccessor();
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideSysMenuBar();
    }

    @Override
    protected void onStop() {
        super.onStop();
        showSysMenuBar();
    }

    protected abstract void onCreateSuccessor();

    protected abstract void onDestroySuccessor();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroySuccessor();
    }

    /**
     * 关闭系统菜单条
     */
    private void hideSysMenuBar(){
        Handler handler=new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendBroadcast(new Intent(ACTION_SYSTEM_BAR_HIDE));
            }
        },TIME_HIDE_BAR_TIME);
    }

    /**
     * 显示系统菜单条
     */
    private void showSysMenuBar(){
        sendBroadcast(new Intent(ACTION_SYSTEM_BAR_SHOW));
    }

    private void forbiddenFace() {
        CruzrFaceApi.initCruzrFace(this);
        CruzrFaceApi.setCruzrFace(null,"clean",true,true);
    }

}
