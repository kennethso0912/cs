/*
 * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 */

package com.ubtechinc.cruiser.wayguide.service;
import com.ubtechinc.cruiser.wayguide.presenter.base.AbstractPresenter;
import com.ubtechinc.cruiser.wayguide.presenter.impl.CommandDispatcherPresenterImpl;
import com.ubtechinc.cruiser.wayguide.presenter.impl.DataHandlerPresenterImpl;
import com.ubtechinc.cruiser.wayguide.presenter.impl.NavPresenterImpl;
import com.ubtechinc.cruiser.wayguide.presenter.impl.WayGuiderPresenterImpl;

import java.util.HashMap;


/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 初始化游览数据，指令分发，游览控制服务
 */

public class ServiceProxy extends ServiceBindable {
	public static final String SERVICE_NAVIGATION = "service_navigation";
	public static final String SERVICE_DISPATCH = "service_dispatch";
	public static final String SERVICE_DATA = "service_data";
	public static final String SERVICE_WAY_GUIDER = "service_way_guider";
	public static final String SERVICE_SPEAK = "service_speak";
	private static final HashMap<String, AbstractPresenter> mServices = new HashMap<>();


	@Override
	protected void onStartOnce() {
		initService(SERVICE_NAVIGATION);
		initService(SERVICE_DISPATCH);
		initService(SERVICE_DATA);
		initService(SERVICE_WAY_GUIDER);

//		initService(SERVICE_DISPATCH);
	}

	public AbstractPresenter initService(String id) {
		if (mServices.containsKey(id)) {
			return mServices.get(id);
		}
		AbstractPresenter p = null;
		switch (id) {
			case SERVICE_DATA:
				  p = new DataHandlerPresenterImpl();
				break;
			case SERVICE_NAVIGATION:
				  p = new NavPresenterImpl();
				break;
			case SERVICE_DISPATCH:
				  p = new CommandDispatcherPresenterImpl(this);
				break;
			case SERVICE_WAY_GUIDER:
				  p = new WayGuiderPresenterImpl();
				break;
//			case SERVICE_SPEAK:移到application最开始的地方 防止调用全局TTS不能用
//				  p = new GlobalSpeakerPresenterImpl();
//				break;
		}
		mServices.put(id, p);
		if(p!=null) {
			p.onCreate();
		}
		return p;
	}

	public static AbstractPresenter getPresenter(String name){
		return mServices.get(name);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		for (AbstractPresenter p : mServices.values()) {
			p.onDestroy();
		}
	}

}
