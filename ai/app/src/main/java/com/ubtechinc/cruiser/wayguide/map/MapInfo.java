/*
 * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 */

package com.ubtechinc.cruiser.wayguide.map;

import java.util.ArrayList;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 地图信息实体类
 */


public class MapInfo {

	public ArrayList<Location> location=new ArrayList<>();

	public static class Location{
		//位置名称
		private String name;
		//X坐标
		private float x;
		//Y坐标
		private float y;

		//转动的速度?
		private double z;
		//动作名称
		private String action;
		//语音内容
		private String content;

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		/**游览任务类型*/
		private int type;

		private String url;

		/**游览图片任务 */
		private long time;


		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		public String getAction() {
			return action;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public void setX(float x) {
			this.x = x;
		}

		public void setY(float y) {
			this.y = y;
		}

		public double getZ() {
			return z;
		}

		public void setZ(double z) {
			this.z = z;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		@Override
		public String toString() {
			return "[name="+name+";x="+x+";y="+y+";content="+content+"]";
		}
	}
}
