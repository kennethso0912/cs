package com.ubtechinc.cruiser.wayguide.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ubtechinc.cruiser.wayguide.event.Event;
import com.ubtechinc.framework.notification.NotificationCenter;

/**
 * Created on 2017/8/2.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc 机器人去充电的广播
 */
public class RobotGotoChargeReceiver extends BroadcastReceiver{
    private static final String TAG = "RobotGotoChargeReceiver";
    public static final String CHARGE_ACTION="com.ubtechinc.cruzrpowersettings.gotocharge";
    @Override
    public void onReceive(Context context, Intent intent) {
          if(CHARGE_ACTION.equals(intent.getAction())){
              Log.e(TAG, "onReceive: 机器人充电信号->");
              notifyEndWayGuider();
          }
    }

    /**
     * 被PC控制后游览过程结束
     */
    private void notifyEndWayGuider(){
        Event event=new Event();
        event.source=Event.EVENT_CMD_NAME_END_SILENCE;
        NotificationCenter.defaultCenter().publish(event);
    }
}
