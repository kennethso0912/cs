/*
 * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 */

package com.ubtechinc.cruiser.wayguide.map;

import com.google.gson.Gson;
import com.ubtechinc.cruiser.wayguide.exception.NoMapException;
import com.ubtechinc.cruiser.wayguide.exception.NoPathException;
import com.ubtechinc.cruiser.wayguide.app.AppApplication;
import com.ubtechinc.cruiser.wayguide.callback.IDataLoadCallback;

import static com.ubtechinc.cruiser.wayguide.utils.ParserUtil.*;
import static com.ubtechinc.cruiser.wayguide.utils.ParserUtil.parseFile;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc 读取配置的默认的游览数据信息
 */

public class MapProvider implements IMapProvider {

	private MapInfo mMaps;


	public MapProvider() {
		//获取游览默认数据
		mMaps = new Gson().fromJson(parseFile(AppApplication.getContext(), FILE_PATH), MapInfo.class);

	}

	@Override
	public MapInfo get() {
		return mMaps;
	}

	@Override
	public MapInfo get(IDataLoadCallback cb) {
		return mMaps;
	}

	@Override
	public void checkData() throws NoMapException, NoPathException {

	}

}
