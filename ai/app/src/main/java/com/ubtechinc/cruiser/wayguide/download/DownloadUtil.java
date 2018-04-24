package com.ubtechinc.cruiser.wayguide.download;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.ubtechinc.cruiser.wayguide.map.MapInfo;
import com.ubtechinc.cruiser.wayguide.service.FetchDataService;
import com.ubtechinc.cruiser.wayguide.task.impl.WayGuiderPicAndVideoTaskImpl;

/**
 * Created on 2017/6/20.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class DownloadUtil {
    /**
     * 根据在线URL获取下载后的本地文件路径
     * @param fileUrl
     * @return
     */
    public static String getLocalFilePathByUrl(Context context,String fileUrl){
        DownloadManager mDownloadManager=(DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        String rst="";
        Uri uri = null;
        DownloadManager.Query query = new DownloadManager.Query();

        DownloadManager.Query baseQuery = query.setFilterByStatus(DownloadManager.STATUS_RUNNING|DownloadManager.STATUS_SUCCESSFUL);


        Cursor cursor = null;
        try {
            cursor = mDownloadManager.query(baseQuery);
            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String url = cursor.getString(
                            cursor.getColumnIndex(DownloadManager.COLUMN_URI));
                    int status = cursor.getInt(
                            cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                    System.out.println("url:" + url+">id="+cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID)));
                    if (url != null && url.equals(fileUrl)) {
                        String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        System.out.println("local_uri:" + fileUri);
                        //TODO 检查下载文件是否存在
                        if (fileUri != null) {
                            rst=fileUri;
                        }
                    }
                }
            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
            Log.i("localapkuri", "ex="+ex.toString());
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return rst;
    }

    /**
     * 下载文件
     * @param context
     * @param url
     * @return
     */
    public long download(Context context,String url){
        DownloadManager dlManager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));
        request.setTitle("download");
        request.setDescription("description");
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, url.substring(url.lastIndexOf("/")+1));
        return dlManager.enqueue(request);
    }
}
