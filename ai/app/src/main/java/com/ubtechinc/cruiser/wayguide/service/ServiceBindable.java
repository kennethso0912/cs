/*
 * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 */

package com.ubtechinc.cruiser.wayguide.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des
 */

public abstract class ServiceBindable extends Service {
	protected boolean started;

	abstract protected void onStartOnce();

	@Override
	public void onCreate() {
		super.onCreate();
		if (!this.started) {
			this.started = true;
			onStartOnce();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
