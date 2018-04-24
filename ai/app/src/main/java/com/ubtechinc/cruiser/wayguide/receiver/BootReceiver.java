package com.ubtechinc.cruiser.wayguide.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.ubtechinc.cruiser.wayguide.map.MapContentObserver;
import com.ubtechinc.cruiser.wayguide.utils.UbtConstant;

/**
 * Created on 2017/8/16.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc  开机启动注册监听地图被删除情况
 */
public class BootReceiver extends BroadcastReceiver{
    private static final String TAG = "BootReceiver";
    private Uri uri=Uri.parse(UbtConstant.ALL_COMMON_POINT);
    private MapContentObserver mapContentObserver=new MapContentObserver(new Handler());
    private ContentResolver mResolver;

    @Override
    public void onReceive(Context context, Intent intent) {
         if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
             Log.e(TAG, "onReceive: 注册监听地图删除数据");
             //监听游览数据变化
             this.mResolver=context.getContentResolver();
             this.mResolver.registerContentObserver(uri,true,mapContentObserver);
         }
    }
}
