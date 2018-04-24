package com.ubtechinc.cruiser.wayguide.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ubtechinc.cruiser.wayguide.service.FetchDataService;

import static android.content.ContentValues.TAG;

/**
 * Created on 2017/6/26.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc  PC同步完数据给MAP，开始去下载游览数据当中的媒体数据(视频，图片)
 */
public class PcSynDataReceiver extends BroadcastReceiver {
    private  static  final String TAG="PcSynDataReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
         Log.e(TAG, "onReceive: 收到PC同步的广播---->>>");
         //开启获取下载数据的服务
         context.startService(new Intent(context,FetchDataService.class));
    }
}
