package com.ubtechinc.cruiser.wayguide.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ubtechinc.cruiser.wayguide.event.Event;
import com.ubtechinc.framework.notification.NotificationCenter;

/**
 * Created on 2017/4/14.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des  游览过程中被PC控制后发出的广播
 */

public class PowerOffReceiver extends BroadcastReceiver{
    private static final String TAG = "PcControllerReceiver";
    //被PC控制发出来的广播
    private static final String ACTION_POWER_OFF= "com.ubtinc.cruzr.action.POWEROFF";
    
    @Override
    public void onReceive(Context context, Intent intent) {
         String action=intent.getAction();
         if(ACTION_POWER_OFF.equals(action)){
             Log.e(TAG, "onReceive: 收到关机广播->");
             notifyEndWayGuider();
         }
    }

    /**
     * 被PC控制后游览过程结束
     */
    private void notifyEndWayGuider(){
        Event event=new Event();
        event.source= Event.EVENT_CMD_NAME_END_SILENCE;
        NotificationCenter.defaultCenter().publish(event);
    }
}
