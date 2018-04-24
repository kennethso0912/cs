/*
 * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 */

package com.ubtechinc.cruiser.wayguide.app;


import android.content.Context;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des
 */

abstract class ServiceContext {
	protected static ServiceContext _instance = null;
	Context mContext;

	public static ServiceContext getInstance() {
		return _instance;
	}

	public ServiceContext(Context context) {
		this.mContext = context;
	}

	Context getApplicationContext() {
		return mContext;
	}

	public abstract void registerSystemObject(String name, Object obj);

}
