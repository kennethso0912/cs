/*
 * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 */

package com.ubtechinc.cruiser.wayguide.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.provider.ContactsContract;
import android.util.Log;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ubtechinc.cruiser.wayguide.app.AppApplication;
import com.ubtechinc.cruzr.sdk.face.StringUtils;
import com.ubtechinc.framework.log.NLog;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 提供assert目录下json文件格式解析工具类
 */

public class ParserUtil {
	private static final String TAG = "ParserUtil";
	public static final String FILE_PATH = "conf/map.js";
	private ParserUtil(){}

    /**
	 * 解析文件为json
	 * @param context
	 * @param path
	 * @return
     */
	public static JsonObject parseFile(Context context, String path) {
		InputStream in = null;
		JsonObject root=null;
		try {
			in = context.openFileInput(FILE_PATH);
		} catch (FileNotFoundException e1) {
			Log.e(TAG, "parseFile: file not found");
		} catch (IllegalArgumentException e) {
			NLog.e(TAG, "read config file failed." + FILE_PATH);
		}
		if (null == in) {
			try {
				AssetManager assets = context.getAssets();
				in = assets.open(FILE_PATH);
			} catch (IOException e) {
				NLog.e(TAG, "read config file failed.");
			}
		}
		JsonElement rootJson;
		final JsonParser jsonParser = new JsonParser();

		try {
			if (in != null) {
				rootJson = jsonParser.parse(new InputStreamReader(in));
				root = rootJson.getAsJsonObject();
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// Ignore.
				}
			}
		}
		return root;
	}
	public static String parseFile1(Context context, String path) {
		InputStream in = null;
		String root=null;
		try {
			in = context.openFileInput(path);
		} catch (FileNotFoundException e1) {
			Log.e(TAG, "parseFile: file not found");
		} catch (IllegalArgumentException e) {
			NLog.e(TAG, "read config file failed." + FILE_PATH);
		}
		if (null == in) {
			try {
				AssetManager assets = context.getAssets();
				in = assets.open(path);
			} catch (IOException e) {
				NLog.e(TAG, "read config file failed.");
			}
		}
		JsonElement rootJson;
		final JsonParser jsonParser = new JsonParser();

		try {
			if (in != null) {
//				rootJson = jsonParser.parse(new InputStreamReader(in));
//				root = rootJson.getAsJsonObject();
				root =convertStreamToString(in);

			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// Ignore.
				}
			}
		}
		return root;
	}

	/**
	 * 获取string根据id名称字符串
	 * @param idName
	 * @return
	 */
	public static String getString(String idName){
		Context context=AppApplication.getContext();
		return context.getResources().getString(context.getResources().getIdentifier(idName,"string",context.getPackageName()));
	}

	public static String getString(int id){
         return AppApplication.getContext().getResources().getString(id);
	}

    public static String getString(int id,Object arg){
		return AppApplication.getContext().getResources().getString(id,arg);
	}

	public static String getString(int id,Object arg1,Object arg2){
		return AppApplication.getContext().getResources().getString(id,arg1,arg2);
	}



	/**
	 * 根据地图数据路径获取地图名和称
	 * @param path 地图路径
	 * @return  地图名称
	 */
	public static String extractMapNameFromPath(String path){
		String mapName="";
		if (StringUtils.isEmpty(path)) {
            return mapName;
		}
		int index=path.lastIndexOf("/");
		if(index>0){
            mapName=path.substring(index+1);
		}
		return mapName;
	}

	public static float stringToFloat(String s){
		Float f=0f;
		try {
			f = Float.valueOf(s);
		}catch(NumberFormatException ex){
			Log.e(TAG, "stringToFloat: wrong format->");
		}
		return f;
	}

	public static String convertStreamToString(InputStream is) {
        /*
          * To convert the InputStream to String we use the BufferedReader.readLine()
          * method. We iterate until the BufferedReader return null which means
          * there's no more data to read. Each line will appended to a StringBuilder
          * and returned as String.
          */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}
}
