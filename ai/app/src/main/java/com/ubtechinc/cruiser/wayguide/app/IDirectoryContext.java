/*
 * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 */

package com.ubtechinc.cruiser.wayguide.app;

import android.content.Context;
import android.os.Environment;

import com.ubtechinc.framework.fs.Directory;
import com.ubtechinc.framework.fs.DirectroyContext;
import com.ubtechinc.framework.util.TimeConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des
 */

public class IDirectoryContext extends DirectroyContext {

	private Context mContext;

	public IDirectoryContext(Context context, String appName) {
		this.mContext = context;
		initContext(appName);
	}

	@Override
	public void initContext(String root) {
		if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			File fileDir = mContext.getFilesDir();
			String rootPath = fileDir.getAbsolutePath() + File.separator + root;
			super.initContext(rootPath);
		} else {
			String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + root;
			super.initContext(rootPath);
		}
	}

	@Override
	protected Collection<Directory> initDirectories() {
		List<Directory> children = new ArrayList<>();

		Directory dir = newDirectory(DirType.log);
		children.add(dir);
		dir = newDirectory(DirType.image);
		children.add(dir);
		dir = newDirectory(DirType.app);
		children.add(dir);
		dir = newDirectory(DirType.cache);
		children.add(dir);
		return children;
	}

	private Directory newDirectory(DirType type) {
		Directory child = new Directory(type.toString(), null);
		child.setType(type.value());
		if (type.equals(DirType.cache)) {
			child.setForCache(true);
			child.setExpiredTime(TimeConstants.ONE_DAY_MS);
		}

		return child;
	}
}
