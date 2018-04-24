package com.ubtechinc.cruiser.wayguide.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import com.ubtechinc.cruiser.wayguide.app.AppApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created on 2017/8/4.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class DownloadMediaObserveReceiver extends BroadcastReceiver {
    private static final String TAG = "DownloadMediaObserve";

    DownloadManager mDownloadManager;
    long pcStartTime=System.currentTimeMillis();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            mDownloadManager=(DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            Cursor cursor = mDownloadManager.query(query);
            if (cursor.moveToFirst()) {
                String url = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                final String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    AppApplication.downloadCount++;
                    Log.e(TAG, "download sucess onReceive:媒体总数="+AppApplication.getMedias().size()+"->downloadCount="+AppApplication.downloadCount+"->url="+url);

                    if(AppApplication.downloadCount==1){
//                           AppApplication.setPcSynStartTime(System.currentTimeMillis());
                         pcStartTime=System.currentTimeMillis();
                    }

                    if(AppApplication.downloadCount==AppApplication.getMedias().size()){
                        Log.e(TAG, "onReceive: 所有媒体数据完全下载成功");
                        AppApplication.downloadCount=0;
                        AppApplication.getMedias().clear();

                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-M-d HH:mm:ss");
                        String time=sdf.format(new Date());
                        AppApplication.setDownloadStatus("媒体下载完成finish :"+time);

                        //发送所有媒体文件下载完成广播
                        context.sendBroadcast(new Intent("com.wayguider.media.download.finished"));

                    }
                }
            }
        }
    }


}
