package com.ubtechinc.cruiser.wayguide.service;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.app.DownloadManager;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.ubtechinc.cruiser.wayguide.app.AppApplication;
import com.ubtechinc.cruiser.wayguide.download.DownloadUtil;
import com.ubtechinc.cruiser.wayguide.model.Media;
import com.ubtechinc.cruiser.wayguide.task.impl.WayGuiderPicAndVideoTaskImpl;
import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;





/**
 * Created on 2017/6/26.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc  取MAP当中的游览数据并下载当中的多媒体数据(如视频，图片)
 */
public class FetchDataService extends IntentService {
    private final static  String TAG="FetchDataService";

    private String[] wayGuideClomuns={"media_type","image_url","video_url"};
     public FetchDataService(){
         super("FetchDataService");
     }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchDataService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private long pcStartTime;

    @Override
    protected void onHandleIntent(Intent intent) {
         //获取游览数据并下载视频图片资源
        Log.e(TAG, "pc 数据同步成功->>"+changeMS2AccurateTime(System.currentTimeMillis()));
        //记录PC同步时间
        pcStartTime=System.currentTimeMillis();

        /**删除同步前的下载数据库的媒体文件数据
        ContentResolver mResolver=this.getContentResolver();
        int row=mResolver.delete(Uri.parse("content://downloads/my_downloads"),null,null);*/

        ArrayList<Media> medias=new ArrayList<>();

        String mapNamePath= RosRobotApi.get().getCurrentMap();

        Log.e(TAG, "mapNamePath="+mapNamePath);

        //当前地图所有媒体点的数据
        Uri ALL_MEDIA_URI = Uri.parse("content://com.ubtechinc.cruzr.map.routinepathProvider/mappointmedia");

        medias=getAllMedias(ALL_MEDIA_URI);

        Log.e(TAG, "all media data: "+medias);

        AppApplication.setMedias(medias);

        AppApplication.getDownloadStatusList().clear();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-M-d HH:mm:ss");
        String time=sdf.format(new Date());
        AppApplication.setDownloadStatus("媒体数据下载start :"+time);

        //测试发开始下载媒体的广播
        sendBroadcast(new Intent("com.wayguider.media.download.start"));

        DownloadManager dlManager= (DownloadManager) FetchDataService.this.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request;
        for(Media md:medias){
            int type=md.getType();
            String url="";
            if(type== WayGuiderPicAndVideoTaskImpl.TYPE_TASK_IMAGE||type==WayGuiderPicAndVideoTaskImpl.TYPE_TASK_TTS_IMAGE) {
                url=md.getImgUrl();
            }else if(type == WayGuiderPicAndVideoTaskImpl.TYPE_TASK_VIDEO) {
                url=md.getVideoUrl();
            }
            if(TextUtils.isEmpty(url)) {
                continue;
            }

            String hadDownFile = DownloadUtil.getLocalFilePathByUrl(this,url);
            if(!TextUtils.isEmpty(hadDownFile)){
                continue;
            }
            request=new DownloadManager.Request(Uri.parse(url));
            request.setTitle("download");
            request.setDescription("description");
//            request.setDestinationInExternalFilesDir(FetchDataService.this, Environment.DIRECTORY_DOWNLOADS, url.substring(url.lastIndexOf("/")+1));
            request.setDestinationInExternalFilesDir(FetchDataService.this, Environment.DIRECTORY_DOWNLOADS, url.substring(url.lastIndexOf("/")+1));
            dlManager.enqueue(request);
        }
        Log.e(TAG, "onHandleIntent: 所有Media文件下载完成");

        //清理同步之前下载过的文件
//        File file=getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
//        deleteFileOfDir(file,pcStartTime);
    }


    /**
     * 返回当前地图对应的所有媒体点的数据
     * @param
     */
    private ArrayList<Media> getAllMediasByMapId(int mapId,Uri uri) {
        ArrayList<Media> medias=new ArrayList<>();
        Media media;
        String[] args={mapId+""};

        ContentResolver resolver=this.getContentResolver();
        Cursor cursor=resolver.query(uri,null, "media_type > 0 and routinepointmediamodel.map_id= ?",args,null);
//        Cursor cursor=resolver.query(uri,null, "media_type > 0 ",null,null);
        if(cursor!=null) {
            Log.e(TAG, "media cursor.getCount()=" + cursor.getCount());
        }
        if (cursor != null&&cursor.moveToFirst()) {
            while (!cursor.isAfterLast()){
                String tts=cursor.getString(cursor.getColumnIndexOrThrow("tts"));
                int media_type=cursor.getInt(cursor.getColumnIndexOrThrow("media_type"));
                String image_url=cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
                String video_url=cursor.getString(cursor.getColumnIndexOrThrow("video_url"));
                long image_play_time=cursor.getLong(cursor.getColumnIndexOrThrow("image_play_time"));

                Log.e(TAG, "获得所有的媒体数据: tts="+tts+";media_type="+media_type+";image_url="+image_url+";video_url="+video_url+";image_play_time="+image_play_time);

                media=new Media();
                media.setTts(tts);
                media.setType(media_type);
                media.setImgUrl(image_url);
                media.setVideoUrl(video_url);
                media.setImgPlayTime(image_play_time);

                medias.add(media);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return medias;
    }

    /**
     * 返回当前地图对应的所有媒体点的数据
     * @param
     */
    private ArrayList<Media> getAllMedias(Uri uri) {
        ArrayList<Media> medias=new ArrayList<>();
        Media media;


        ContentResolver resolver=this.getContentResolver();
        Cursor cursor=resolver.query(uri,null, "media_type > 0 ",null,null);
//        Cursor cursor=resolver.query(uri,null, "media_type > 0 ",null,null);
        if(cursor!=null) {
            Log.e(TAG, "media cursor.getCount()=" + cursor.getCount());
        }
        if (cursor != null&&cursor.moveToFirst()) {
            while (!cursor.isAfterLast()){
                String tts=cursor.getString(cursor.getColumnIndexOrThrow("tts"));
                int media_type=cursor.getInt(cursor.getColumnIndexOrThrow("media_type"));
                String image_url=cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
                String video_url=cursor.getString(cursor.getColumnIndexOrThrow("video_url"));
                long image_play_time=cursor.getLong(cursor.getColumnIndexOrThrow("image_play_time"));

                Log.e(TAG, "获得所有的媒体数据: tts="+tts+";media_type="+media_type+";image_url="+image_url+";video_url="+video_url+";image_play_time="+image_play_time);

                media=new Media();
                media.setTts(tts);
                media.setType(media_type);
                media.setImgUrl(image_url);
                media.setVideoUrl(video_url);
                media.setImgPlayTime(image_play_time);

                medias.add(media);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return medias;
    }

    /**
     *根据地图名获取地图ID
     * @param mapName
     * @return
     */
    private int getMapIdByName(String mapName){
       int mapId=-1;
        String[] args={mapName};
        String[] res={"map_id","map_name"};

        Uri uri = Uri.parse("content://com.ubtechinc.cruzr.map.routinepathProvider/routinepath");
        ContentResolver resolver=this.getContentResolver();
        Cursor cursor=resolver.query(uri,res,"map_name= ? limit 0,1",args,null);
//        Cursor cursor=resolver.query(uri,res,null,null,null);http://localhost:8080/Space/product_input

        if(cursor!=null) {
            Log.e(TAG, "cursor.getCount()=" + cursor.getCount());
        }

        if (cursor != null&&cursor.moveToFirst()){
            mapId= cursor.getInt(cursor.getColumnIndexOrThrow("map_id"));
            String map_name=cursor.getString(cursor.getColumnIndexOrThrow("map_name"));
            Log.e(TAG, "获取所有普通点的数据: mapid="+mapId+",map_name="+map_name);

            cursor.close();
        }
        return mapId;

    }

    private String changeMS2AccurateTime(long millSecond){
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = dateformat.format(millSecond);
        return dateStr;
    }

    /**
     * 循环删除了过期媒体文件
     * @param dir
     * @param expireTime
     */
    private  void deleteFileOfDir(File dir, long expireTime) {
        Log.e(TAG, "deleteFileOfDir: expireTime="+changeMS2AccurateTime(expireTime)+";expireTime="+expireTime);
        File file;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                file=new File(dir, children[i]);
                Log.e(TAG, "deleteFileOfDir: file.lastModified()="+changeMS2AccurateTime(file.lastModified())+";file.getName()="+file.getName());
                if(file.lastModified()<expireTime){
                    Log.e(TAG, "deleteFileOfDir: file.lastModified()<expireTime:"+expireTime);
                    file.delete();
                }
            }
        }

    }
}
