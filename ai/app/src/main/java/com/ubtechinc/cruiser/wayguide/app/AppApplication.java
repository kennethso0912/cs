/*
 * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 */

package com.ubtechinc.cruiser.wayguide.app;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.bugly.crashreport.CrashReport;
import com.ubtechinc.cruiser.wayguide.interactor.impl.CommandNewDispatcherInteractorImpl;
import com.ubtechinc.cruiser.wayguide.model.Media;
import com.ubtechinc.cruiser.wayguide.presenter.base.AbstractPresenter;
import com.ubtechinc.cruiser.wayguide.presenter.impl.GlobalSpeakerPresenterImpl;
import com.ubtechinc.cruiser.wayguide.service.ServiceProxy;

import com.ubtechinc.cruiser.wayguide.utils.SettingsDataUtil;
import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;
import com.ubtechinc.cruzr.sdk.status.StatusInitCallBack;
import com.ubtechinc.cruzr.sdk.status.SystemStatusApi;
import com.ubtechinc.cruzr.serverlibutil.interfaces.InitListener;
import com.ubtechinc.framework.log.Logger;
import com.ubtechinc.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static com.ubtechinc.cruiser.wayguide.utils.UbtConstant.BUGLY_APP_ID;


/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Description
 */

public class AppApplication extends Application {
	private static final String TAG = "AppApplication";
	private static Context mContext = null;
	public static int downloadCount=0;//count of media downloaded
	public static boolean isChatTTs=false;
	private static HashMap<String,ArrayList<Activity>> actsMap=new HashMap<>();
	private static HashMap<String,ArrayList<Service>> servicesMap=new HashMap<>();
	private static List<Media> curMedias=new ArrayList<>();
	private static List<String> downloadStatusList=new ArrayList<>();
	private static Map<String,ArrayList<Media>> mediasWithAllTask=new Hashtable<>();//缓存游览过程 对应任务名称的媒体队列集合


	public static Context getContext() {
		return mContext;
	}

	public synchronized void setContext(Context context) {
		AppApplication.mContext = context;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		NLog.e(TAG,"onCreate()->");

		init();

		//启动游览相关服务(注：必须在init()调用之后才可调用，因为注册ros callback前必须先初始化ros)
		startService(new Intent(this,ServiceProxy.class));

        set();

        configImageLoader();
	}

	private static InitListener mRosInitListener=new InitListener() {
		@Override
		public void onInit() {
			Log.e(TAG, "onInit: RosUtil 初始化成功->");
		}

		@Override
		public void onReConnect() {
			super.onReConnect();
		}
	};




	/**
	 * 添加相关游览点任务名的媒体播放界面
	 * @param taskName 游览点任务名
	 * @param act  媒体界面activity名称
	 */
	public static void  addActivity(String taskName,Activity act){
		ArrayList<Activity> acts=actsMap.get(taskName);
		if(acts==null){
			acts=new ArrayList<>();
		}
		acts.add(act);
		actsMap.put(taskName,acts);
	}

	/**
	 * 根据游览点任务名关闭当前任务所有媒体播放界面
	 * @param taskName 游览点任务名
	 */
	public static void removeActsByTaskName(String taskName){
		ArrayList<Activity> acts=actsMap.get(taskName);
		if(acts==null||acts.isEmpty()){
			  Log.e(TAG, "removeActsByTaskName: not any ui existed");
			return;
		}

		for (Activity act:acts) {
				act.finish();
		}
		acts.clear();

		actsMap.remove(taskName);
	}



	public static void clearAllUI(){
		if(!actsMap.isEmpty()){
			Collection<ArrayList<Activity>> collection=actsMap.values();
			finishAllUI(collection);

			actsMap.clear();
		}
	}

	private static void finishAllUI(Collection<ArrayList<Activity>> co){
		for (ArrayList<Activity> arr:co){
			for (Activity act:arr) {
				act.finish();
			}
		}
	}




	/**
	 * 添加相关游览点任务名的媒体播放界面
	 * @param taskName 游览点任务名
	 * @param serv  媒体界面activity名称
	 */
	public static void  addService(String taskName,Service serv){
		ArrayList<Service> acts=servicesMap.get(taskName);
		if(acts==null){
			acts=new ArrayList<>();
		}
		acts.add(serv);
		servicesMap.put(taskName,acts);
	}

	/**
	 * 根据游览点任务名关闭当前任务所有媒体播放界面
	 * @param taskName 游览点任务名
	 */
	public static void removeServicesByTaskName(String taskName){
		ArrayList<Service> acts=servicesMap.get(taskName);
		if(acts==null||acts.isEmpty()){
			Log.e(TAG, "removeActsByTaskName: not any ui existed");
			return;
		}

		for (Service act:acts) {
			act.stopSelf();
		}
		acts.clear();

		servicesMap.remove(taskName);
	}

	public static void clearAllService(){
		if(!servicesMap.isEmpty()){
			Collection<ArrayList<Service>> collection=servicesMap.values();
			finishAllService(collection);

			actsMap.clear();
		}
	}

	private static void finishAllService(Collection<ArrayList<Service>> co){
		for (ArrayList<Service> arr:co){
			for (Service act:arr) {
				act.stopSelf();
			}
		}
	}


	/**
	 * 应用初始化api资源
	 */
	private void init(){
		//解析游览本地指令类型
		CommandNewDispatcherInteractorImpl.parseLocalCmd(this);

		//初始化全局TTS(注册TTS事件)
		AbstractPresenter p = new GlobalSpeakerPresenterImpl();
		p.onCreate();


		//初始化导航ROS服务
		RosRobotApi.get().initializ(this.getApplicationContext(),mRosInitListener);

		//初始化注册状态机服务
		//SystemStatusApi.get().init(this);
		SystemStatusApi.get().init(this, new StatusInitCallBack() {
			@Override
			public void onInit() {
				Log.e(TAG, "onInit: SystemStatusApi-->>");
			}
		});


		//实例化文件夹
		ACContext.initInstance(getContext());

		CrashReport.initCrashReport(getApplicationContext(), BUGLY_APP_ID, true);

	}

	private void set(){
		//设置日志系统
		NLog.setDebug(true, Logger.VERBOSE);
		NLog.trace(Logger.TRACE_REALTIME, null);

		setContext(this.getApplicationContext());
		SettingsDataUtil.get().loadSettingsData(this);
	}



	public static void  setMedias(List<Media> medias){
          curMedias=medias;
	}

	public static List<Media> getMedias(){
		return curMedias;
	}

	//status of media file's download
	public static void  setDownloadStatus(String downloadStatus){
         if(downloadStatusList==null){
			 downloadStatusList=new ArrayList<>();
		 }
		downloadStatusList.add(downloadStatus);
	}

	public static List<String> getDownloadStatusList(){
		return downloadStatusList;
	}


	public static void addMediasWithTaskName(String taskName,ArrayList<Media> medias){
		mediasWithAllTask.put(taskName,medias);
	}

	public static ArrayList<Media> getMediasByTaskName(String taskName){
		return mediasWithAllTask.get(taskName);
	}



	private void configImageLoader(){
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY-2)
				.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				//.writeDebugLogs()
				.memoryCache(new WeakMemoryCache()).build();

		ImageLoader.getInstance().init(config);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.e(TAG, "onConfigurationChanged: language change-->>");
		//语言切换重新解析游览本地指令类型
		CommandNewDispatcherInteractorImpl.parseLocalCmd(this);
	}
}
