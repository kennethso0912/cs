/*
 * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 */

package com.ubtechinc.cruiser.wayguide.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.ubtechinc.cruiser.wayguide.R;
import com.ubtechinc.cruzr.sdk.recognition.utils.ImageUtils;


/**
 * @date 2017/3/17
 * @author KennethSo
 * @Description
 * @modifier
 * @modify_time
 */

public class ImageActivity extends Activity {
	private static final String TAG = "MainActivity";
	private ImageView mIvShow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.pic);

		sendBroadcast(new Intent("com.ubt.cruzr.hidebar"));

		initView();

		setView();

	}

	private void initView(){
		mIvShow=(ImageView) findViewById(R.id.iv_show);


	}

	private void setView(){
		String filePath="/sdcard/smart_intro/image/shutu.jpg";//131953sdgxz8nnx8ze98nk.jpg "file:///mnt/sdcard/image.png"
		String filePath1="file:///sdcard/cruzr/image/652541.jpg";
//		String filePath1="file:///sdcard/cruzr/image/proteus-01.jpg";
//		mIvShow.setImageURI(Uri.parse(filePath));
		mIvShow.setScaleType(ImageView.ScaleType.FIT_CENTER);


		Bitmap bitmap= BitmapFactory.decodeFile(Uri.parse(filePath1).getPath());
//		int height= px2dp(this,bitmap.getHeight());
//		int width= px2dp(this,bitmap.getWidth());

		int height= bitmap.getHeight();
		int width= bitmap.getWidth();
		bitmap.recycle();

//		if(width>=1920&&height>=1080){
//
//		}else {
			if(height>=width){
				int tempWidth=width*1080/height;
				if(tempWidth>1920){
					height=height*1920/width;
					width=1920;
				}else {
					width=tempWidth;
					height = 1080;
				}
			}else if(height<width){
				int tempHeight=height*1920/width;
				if(tempHeight>1080){
					width=width*1080/height;
					height=1080;
				}else {
					height=tempHeight;
					width = 1920;
				}
			}

			mIvShow.setPadding(-1, -1, -1, -1);
			Log.e(TAG, "setView: h:" + height + "w:" + width);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
			lp.setMargins(-1, -1, -1, -1);
			mIvShow.setLayoutParams(lp);
//		}


//		Log.e(TAG, "mIvShow: h:"+mIvShow.getHeight()+"w:"+mIvShow.getWidth());
		ImageLoader.getInstance().displayImage(filePath1,mIvShow,OPTIONS);

//		mIvShow.setImageURI(Uri.parse(filePath1));
	}

	private static final DisplayImageOptions OPTIONS = new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.imageScaleType(ImageScaleType.NONE)
			.displayer(new RoundedBitmapDisplayer(0)).build();

	public void start(View view){
		sendBroadcast(new Intent("com.ubtechinc.cruzr.intent.action.START_WAYGUIDE"));
	}

	private int px2dp(Context context, float pxValue){
		float scale=context.getResources().getDisplayMetrics().density;
		return (int)(pxValue/scale+0.5f);
	}
}
