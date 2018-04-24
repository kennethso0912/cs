/*
 * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 */

package com.ubtechinc.cruiser.wayguide.map;

import com.ubtechinc.cruiser.wayguide.model.Media;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 地图信息实体类
 */


public class MapNewInfo {

	public ArrayList<Location> location=new ArrayList<>();

	public Location returnPoint=null;

	public static class Location{
		private int map_point_id;//包含媒体的游览点的ID
		//位置名称
		private String name;
		//X坐标
		private float x;
		//Y坐标
		private float y;
		//角度
		private float theta;
        //一个点的所有媒体集合
		private ArrayList<Media> medias;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public float getX() {
			return x;
		}

		public void setX(float x) {
			this.x = x;
		}

		public float getY() {
			return y;
		}

		public void setY(float y) {
			this.y = y;
		}

		public float getTheta() {
			return theta;
		}

		public void setTheta(float theta) {
			this.theta = theta;
		}

		public ArrayList<Media> getMedias() {
			return medias;
		}

		public void setMedias(ArrayList<Media> medias) {
			this.medias = medias;
		}

		public int getMap_point_id() {
			return map_point_id;
		}

		public void setMap_point_id(int map_point_id) {
			this.map_point_id = map_point_id;
		}

		private int map_id;

		private int path_id;

		public int getMap_id() {
			return map_id;
		}

		public void setMap_id(int map_id) {
			this.map_id = map_id;
		}

		public int getPath_id() {
			return path_id;
		}

		public void setPath_id(int path_id) {
			this.path_id = path_id;
		}

		@Override
		public String toString() {
			return "[name="+name+";map_point_id="+map_point_id+";x="+x+";y="+y+";theta="+theta+";map_id="+map_id+";path_id="+path_id+";medias="+medias+"]";
		}
	}
}
