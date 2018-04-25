/*
 * Copyright (c) 2008-2016 UBT Corporation.  All rights reserved.  Redistribution,
 *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 */

package com.ubtechinc.cruiser.wayguide.ui;


import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ubtechinc.cruiser.wayguide.R;
import com.ubtechinc.cruiser.wayguide.app.AppApplication;
import com.ubtechinc.cruiser.wayguide.event.Event;
import com.ubtechinc.cruiser.wayguide.event.UIStateEvent;

import com.ubtechinc.cruiser.wayguide.ui.base.BaseSysBarActivity;
import com.ubtechinc.framework.notification.NotificationCenter;
import com.ubtechinc.framework.notification.Subscriber;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


/**
 * @date 2017/3/17
 * @author KennethSo
 * @Description  游览状态控制界面
 * @modifier
 * @modify_time
 */

public class ControlActivity extends BaseSysBarActivity {
	private static final String TAG = "Control";
    private Button mBtnPause;
    private Button mBtnContinu;
    private TextView mTvStatus;

    private GifImageView mGifImageView;
    private GifDrawable mGifDrawable;

    @Override
    protected void onCreateSuccessor() {
        setContentView(R.layout.activity_control);
        init();
    }

    @Override
    protected void onDestroySuccessor() {
        unRegEventForResult();
    }

    private void initView(){
        mBtnContinu= (Button) findViewById(R.id.btn_continue);
        mBtnPause= (Button) findViewById(R.id.btn_pause);
        mTvStatus=findViewById(R.id.tv_status);
    }

    private void init(){
        initView();
        regEventForResult();

        AppApplication.addActivity("main",this);

//        initGif();
    }


    private void initGif(){
        mGifImageView =(GifImageView) findViewById(R.id.giv_demo);
//        try {

//            mGifDrawable = new GifDrawable(getResources(), R.drawable.status_anim);
//            mGifImageView.setImageDrawable(mGifDrawable);

//
//        } catch (Resources.NotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

	public void pause(View view){
        Log.e(TAG, "pause: ->");
        publishEvent(Event.EVENT_CMD_NAME_PAUSE);
    }

    public void continu(View view){
        Log.e(TAG, "continu: ->");
        publishEvent(Event.EVENT_CMD_NAME_CONTINUE);
    }

    public void end(View view){
        Log.e(TAG, "end: ->");
        publishEvent(Event.EVENT_CMD_NAME_END);
    }


    private void publishEvent(String cmd) {
        Event event=new Event();
        event.source=cmd;
        NotificationCenter.defaultCenter().publish(event);
    }


    private void switchBtn(int state){
        mBtnContinu.setVisibility(state==UIStateEvent.TYPE_STATE_PAUSE?View.VISIBLE:View.GONE);
        mBtnPause.setVisibility(state==UIStateEvent.TYPE_STATE_PAUSE?View.GONE:View.VISIBLE);

        if(state==UIStateEvent.TYPE_STATE_PAUSE){
            mTvStatus.setText(R.string.ping);
            mGifDrawable.pause();
        }else {
            mTvStatus.setText(R.string.ing);
            mGifDrawable.start();
        }
    }

    /**
     * 注册监控游览操作结果
     */
    private void regEventForResult(){
        NotificationCenter.defaultCenter().subscriber(UIStateEvent.class,mSubStateFeedBack);
    }

    /**
     * 取消注册监控游览操作结果
     */
    private void unRegEventForResult(){
        NotificationCenter.defaultCenter().unsubscribe(UIStateEvent.class,mSubStateFeedBack);
    }

    /**
     * 游览回调结果
     */
    private Subscriber<UIStateEvent> mSubStateFeedBack=new Subscriber<UIStateEvent>() {
        @Override
        public void onEvent(UIStateEvent event) {
              Log.e(TAG, "onEvent: event.state="+event.state+";event.rst="+event.rst);
              switch (event.state){
                  case UIStateEvent.TYPE_STATE_PAUSE:
                  case UIStateEvent.TYPE_STATE_CONTINUE:
                         if(event.rst==UIStateEvent.RESULT_SUCCESS){
                             Log.e(TAG, "onEvent: pause or continue ->RESULT_SUCCESS");
                             switchBtn(event.state);
                         }else if(event.rst==UIStateEvent.RESULT_FAIL){
                             Log.e(TAG, "onEvent: pause or continue ->RESULT_FAIL");
                         }
                      break;
                  case UIStateEvent.TYPE_STATE_END:
                        if(event.rst==UIStateEvent.RESULT_SUCCESS){
                          Log.e(TAG, "onEvent:  end ->RESULT_SUCCESS");
                          ControlActivity.this.finish();
                        }else if(event.rst==UIStateEvent.RESULT_FAIL){
                          Log.e(TAG, "onEvent:  end ->RESULT_FAIL");
                        }
                      break;
                  default:
                      break;
              }
        }
    };


}
