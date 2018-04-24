/*
 * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 */

package com.ubtechinc.cruiser.wayguide.map;

import com.ubtechinc.cruiser.wayguide.exception.NoMapException;
import com.ubtechinc.cruiser.wayguide.exception.NoPathException;
import com.ubtechinc.cruiser.wayguide.callback.IDataLoadCallback;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 地图编辑数据获取接口
 */


public interface IMapProvider<T>{
	T get();
	T get(IDataLoadCallback cb);

	//检测游览数据（地图和游览路线）
	void checkData() throws NoMapException , NoPathException;
}
