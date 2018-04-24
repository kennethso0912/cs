package com.ubtrobot.service;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ubtrobot.led.LedApi;
import com.ubtrobot.led.listener.ResponseListener;
import com.ubtrobot.led.protos.LedWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @desc :
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/9/8
 * @modifier:
 * @modify_time:
 */

public class MainActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "ONLY_LED_SAMPLE";
    private Button mbtnMounthOn,mbtnMounthOff;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){
        mbtnMounthOn=(Button)findViewById(R.id.btnOn);
        mbtnMounthOff=(Button)findViewById(R.id.btnOff);

        mbtnMounthOn.setOnClickListener(this);
        mbtnMounthOff.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.btnOn:
                 Map<String,String> effects=new HashMap<String,String>();
                 effects.put("period","10");
                 effects.put("gap","10");
                 effects.put("duration","5");
                 LedWrapper.Led ledWithBreath = LedWrapper.Led.newBuilder().setLId(0).setBright(1).putAllEffect(effects).setColor(Color.argb(0, 0, 255, 0)).build();
                 ArrayList<LedWrapper.Led> leds = new ArrayList<LedWrapper.Led>();
                 leds.add(ledWithBreath);
                 LedApi ledApi = LedApi.get();
                 ledApi.setOn(leds, new ResponseListener<Void>() {
                     @Override
                     public void onResponseSuccess(Void data) {

                     }

                     @Override
                     public void onFailure(int errCode, @Nullable String errMsg) {

                     }
                 });
                 break;
             case R.id.btnOff:
                 Log.e(TAG, "onClick: btnOff->");
                 List<Integer> ledIds = new ArrayList<Integer>(1);
                 ledIds.add(0);
                 LedApi ledApi1 = LedApi.get();
                 ledApi1.setOff(ledIds, new ResponseListener<Void>() {
                     @Override
                     public void onResponseSuccess(Void data) {

                     }

                     @Override
                     public void onFailure(int errCode, @Nullable String errMsg) {

                     }
                 });
                 break;
         }
    }


    private void openMounthLed(){
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("call", "闭上眼睛");
//        ServiceBus.get().getBusApplicationCluster().queryApplicationCalls("speech", data, new BusApplicationCluster.QueryCallback() {
//            @Override
//            public void onQueried(List<ApplicationCall> list) {
////                Log.e(TAG, "onQueried: ", );
//                list.size();
//
//
////                BusApplication busApplication = ServiceBus.get().getBusApplication("");
////                busApplication.call("")
//            }
//        });
    }

    private void closeMountthLed(){

    }

}
