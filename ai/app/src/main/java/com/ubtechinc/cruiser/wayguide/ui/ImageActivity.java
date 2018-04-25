/*
 * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 */

package com.ubtechinc.cruiser.wayguide.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ubtechinc.cruiser.wayguide.R;


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
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.pic);

		sendBroadcast(new Intent("com.ubt.cruzr.hidebar"));

		mIvShow=(ImageView) findViewById(R.id.iv_show);
		String filePath="/sdcard/smart_intro/image/123.jpeg";//131953sdgxz8nnx8ze98nk.jpg
		mIvShow.setImageURI(Uri.parse(filePath));

		mHandler=new Handler();

		HandlerThread ht=new HandlerThread("test");
		ht.start();
	}



}
