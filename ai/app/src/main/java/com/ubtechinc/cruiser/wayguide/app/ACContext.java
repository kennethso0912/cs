/*
 * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 */

package com.ubtechinc.cruiser.wayguide.app;

import android.content.Context;
import com.ubtechinc.framework.fs.DirectoryManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des
 */


class ACContext extends ServiceContext {

	private final static String GC_ROOT_FOLDER = "smart_intro";
	private static final String DIR_MANAGER = "dir";

	static boolean initInstance(Context context) {
		if (_instance == null) {
			ACContext gcContext = new ACContext(context);

			_instance = gcContext;
			return gcContext.init();
		}

		return true;
	}

	private Map<String, Object> objsMap;


	private ACContext(Context context) {
		super(context);
		objsMap = new HashMap<>();
	}

	private boolean init() {
		DirectoryManager dm = new DirectoryManager(new ACDirectoryContext(getApplicationContext(), GC_ROOT_FOLDER));
		boolean ret = dm.buildAndClean();
		if (!ret) return false;

		registerSystemObject(DIR_MANAGER, dm);

		return true;
	}


	@Override
	public void registerSystemObject(String name, Object obj) {
		objsMap.put(name, obj);
	}


}
