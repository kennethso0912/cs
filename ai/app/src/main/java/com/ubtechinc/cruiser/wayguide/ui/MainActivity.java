/*
 * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 */

package com.ubtechinc.cruiser.wayguide.ui;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import com.ubtechinc.cruiser.wayguide.R;
import com.ubtechinc.cruiser.wayguide.adapter.DownLoadingAdapter;
import com.ubtechinc.cruiser.wayguide.app.AppApplication;
import com.ubtechinc.cruiser.wayguide.map.IMapProvider;
import com.ubtechinc.cruiser.wayguide.map.MapInfo;
import com.ubtechinc.cruiser.wayguide.map.MapNewDBProvider;
import com.ubtechinc.cruiser.wayguide.map.MapNewInfo;
import com.ubtechinc.cruiser.wayguide.map.MapProvider;
import com.ubtechinc.cruzr.serverlibutil.interfaces.RemoteStatusListener;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ubtechinc.cruiser.wayguide.utils.ParserUtil.stringToFloat;


/**
 * @date 2017/3/17
 * @author KennethSo
 * @Description
 * @modifier
 * @modify_time
 */

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	private VideoView mVvShow;
	private Button mBtnDownload;
	private Button mBtnQuery;
	private TextView mTvQueryRst;
	private TextView mTvDownloadRst;

	private ListView mLvMedias;

	private Button mBtnDelAll;

	private Button mBtnQueryAllPoint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		init();
		initView();

	    IntentFilter downloadIF=new IntentFilter();
		downloadIF.addAction("com.wayguider.media.download.start");
		downloadIF.addAction("com.wayguider.media.download.finished");
		registerReceiver(mRefreshDownloadStatus,downloadIF);

		freshDownloadStatus();

		mBtnQueryAllPoint=(Button) findViewById(R.id.btn_query_all_point);
		mBtnQueryAllPoint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                getPointsByMap(MainActivity.this.getContentResolver());
			}
		});

	}

	/**
	 * 获取当前地图当前路线的所有普通点
	 * @param resolver
	 */
	private ArrayList<MapNewInfo.Location> getPointsByMap(ContentResolver resolver) {
		ArrayList<MapNewInfo.Location> locs = new ArrayList<>();
		MapNewInfo.Location loc;
		String[] args={"true"};

		Uri ALL_POINT_URI = Uri.parse("content://com.ubtechinc.cruzr.map.routinepathProvider/routinepath");

		String[] wayGuideClomuns={"pointName","map_x","map_y","point_index","map_name","map_id","routine_id","map_point_id","theta"};

		Cursor cursor=resolver.query(ALL_POINT_URI,wayGuideClomuns,"0==0 group by mapmodel.id", null , null);
		if (cursor != null&&cursor.moveToFirst()) {
			while (!cursor.isAfterLast()){
				String point_name=cursor.getString(cursor.getColumnIndexOrThrow("pointName"));
				float map_x=stringToFloat(cursor.getString(cursor.getColumnIndexOrThrow("map_x")));
				float map_y=stringToFloat(cursor.getString(cursor.getColumnIndexOrThrow("map_y")));
				float theta=stringToFloat(cursor.getString(cursor.getColumnIndexOrThrow("theta")));

				int map_point_id=cursor.getInt(cursor.getColumnIndexOrThrow("map_point_id"));

				String map_name=cursor.getString(cursor.getColumnIndexOrThrow("map_name"));

				int map_id=cursor.getInt(cursor.getColumnIndexOrThrow("map_id"));

//                int path_id=cursor.getInt(cursor.getColumnIndexOrThrow("path_id"));routine_id就是path_id
				int path_id=cursor.getInt(cursor.getColumnIndexOrThrow("routine_id"));

				Log.e(TAG, "getPointsByMap-> point_name="+point_name+",map_x="+map_x+",map_y="+map_y+",map_point_id="+map_point_id+",theta="+theta+",map_name="+map_name+",map_id="+map_id+",path_id="+path_id);

				loc=new MapNewInfo.Location();
				loc.setName(point_name);
				loc.setX(map_x);
				loc.setY(map_y);
				loc.setTheta(theta);
				loc.setMap_point_id(map_point_id);
				loc.setMap_id(map_id);
				loc.setPath_id(path_id);

				locs.add(loc);

				cursor.moveToNext();
			}
			cursor.close();
		}
		return locs;
	}

	private void initView(){
		mBtnDownload=(Button) findViewById(R.id.btn_start_load);

		mBtnQuery= (Button) findViewById(R.id.btn_start_query);

		mTvQueryRst=(TextView) findViewById(R.id.tv_query_result);
		mTvDownloadRst=(TextView) findViewById(R.id.tv_download_result);

		mBtnDownload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				loadAllData();
			}
		});

		mBtnQuery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				queryAll();
			}
		});

		mBtnDelAll=(Button) findViewById(R.id.btn_del_all);
		mBtnDelAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                deleteAllOfDownloadData();
			}
		});

		mLvMedias=(ListView) findViewById(R.id.lv_medias_download);
		handleDownloadsChanged(0);
	}

	public static final String AUTHORITY = "com.android.app.downloads";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/my_downloads");//content://downloads/my_downloads


	private void deleteAllOfDownloadData(){
		ContentResolver mResolver=this.getContentResolver();
		int row=mResolver.delete(Uri.parse("content://downloads/my_downloads"),null,null);
		Log.e(TAG, "deleteAllOfDownloadData: row="+row);

		//delete media file in sdcard
		File file=getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        deleteDir(file);


	}


	private void queryAll(){
		String rst="";
		Uri uri = null;
		DownloadManager.Query query = new DownloadManager.Query();
		DownloadManager.Query baseQuery = query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);

		Cursor cursor = null;
		try {
			cursor = mDownloadManager.query(baseQuery);

			if (cursor != null && cursor.getCount() > 0) {
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String id=cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
					String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
					String modifiedTime=cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP));
					rst+=id+"  "+fileUri+" "+changeMS2AccurateTime(Long.parseLong(modifiedTime))+"\n";

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
		mTvQueryRst.setText(rst);

	}


    private void init(){
		mDownloadManager=(DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
	}

	DownloadManager mDownloadManager;
	/**
	 *加载游览数据，和游览媒体的数据
	 */
	private void loadAllData(){
		ContentResolver mResolver=this.getContentResolver();
		int row=mResolver.delete(Uri.parse("content://downloads/my_downloads"),null,null);
		Log.e(TAG, "deleteAllOfDownloadData: row="+row);

		long expiredTime=System.currentTimeMillis();
		IMapProvider mapProvider = new MapProvider();
		MapInfo mapInfo=(MapInfo)mapProvider.get();

        ArrayList<MapInfo.Location> locations=mapInfo.location;

		DownloadManager.Request request=null;

		AppApplication.getDownloadStatusList().clear();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-M-d HH:mm:ss");
		String time=sdf.format(new Date());
		AppApplication.setDownloadStatus("媒体数据下载start :"+time);

		//测试发开始下载媒体的广播
		sendBroadcast(new Intent("com.wayguider.media.download.start"));

		for (MapInfo.Location loc:locations){
			if(loc.getType()!=0){
				String url=loc.getUrl();
				if(!TextUtils.isEmpty(query(url))){
					   Log.e(TAG, "data exist->url:"+url);
					continue;
				}
                request=new DownloadManager.Request(Uri.parse(loc.getUrl()));
				request.setTitle("download");
				request.setDescription("description");
//				request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS, url.substring(url.lastIndexOf("/")+1));
				request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, url.substring(url.lastIndexOf("/")+1));
                mDownloadManager.enqueue(request);

			}
		}

	}

	/**
	 * 查询对应资源是否已下载
	 * @param fileUrl
	 * @return
	 */
	private String query(String fileUrl){
		String rst="";
		Uri uri = null;
		DownloadManager.Query query = new DownloadManager.Query();

		DownloadManager.Query baseQuery = query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);

		Cursor cursor = null;
		try {
			cursor = mDownloadManager.query(baseQuery);
			if (cursor != null && cursor.getCount() > 0) {
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					String url = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
					int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

					System.out.println("url:" + url+">id="+cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID)));
					if (url != null && url.equals(fileUrl)) {
						String fileUri = cursor.getString(
								cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
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


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mRefreshDownloadStatus!=null){
			unregisterReceiver(mRefreshDownloadStatus);
		}
	}

	private BroadcastReceiver mRefreshDownloadStatus = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if("com.wayguider.media.download.start".equals(intent.getAction())||"com.wayguider.media.download.finished".equals(intent.getAction())){
				  freshDownloadStatus();
			}
		}
	};

	private void freshDownloadStatus() {
		List<String> dlStatusList= AppApplication.getDownloadStatusList();
		StringBuffer sb=new StringBuffer();
		for (String str: dlStatusList) {
			sb.append(str+"\n");
		}
		mTvDownloadRst.setText(sb.toString());
	}

	/**
	 * 递归删除目录下的所有文件及子目录下所有文件
	 * @param dir 将要删除的文件目录
	 * @return boolean Returns "true" if all deletions were successful.
	 *                 If a deletion fails, the method stops attempting to
	 *                 delete and returns "false".
	 */
	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
            //递归删除目录中的子目录下
			for (int i=0; i<children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// 目录此时为空，可以删除
		return dir.delete();
	}


	private Cursor mCursor;
	private DownLoadingAdapter mDownloadingAdapter;

	public void handleDownloadsChanged(int i){
		DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterByStatus(DownloadManager.STATUS_RUNNING
				| DownloadManager.STATUS_PAUSED
				| DownloadManager.STATUS_PENDING|DownloadManager.STATUS_SUCCESSFUL);
		try {
			Field field = DownloadManager.Query.class.getDeclaredField("mOrderByColumn");
			field.setAccessible(true);
			field.set(query, "_id");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		mCursor = mDownloadManager.query(query);

		if(mCursor != null) {
			if (i == 0) {
				mDownloadingAdapter = new DownLoadingAdapter(MainActivity.this, mCursor);
				mLvMedias.setAdapter(mDownloadingAdapter);

			} else {
				Log.e(TAG,"handleDownloadsChanged " + i);

			}
		}

	}

	private String changeMS2AccurateTime(long millSecond){
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = dateformat.format(millSecond);
		return dateStr;
	}

	/**
	 * 测试获取返回位置名称
	 * @param view
	 */
	public void testMapProviderData(View view){
		IMapProvider mapProvider=new MapNewDBProvider(this);
		mapProvider.get(null);
	}


}
