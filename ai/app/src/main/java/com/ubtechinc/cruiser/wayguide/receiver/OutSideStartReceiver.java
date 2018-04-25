package com.ubtechinc.cruiser.wayguide.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ubtechinc.cruiser.wayguide.event.Event;
import com.ubtechinc.cruiser.wayguide.presenter.impl.WayGuiderPresenterImpl;
import com.ubtechinc.cruiser.wayguide.service.ServiceProxy;
import com.ubtechinc.framework.notification.NotificationCenter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created on 2017/9/11.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class OutSideStartReceiver extends BroadcastReceiver
{
    private static final String TAG = "OutSideStartReceiver";
    private static final String INTENT_ACTION_START_WAYGUIDE="com.ubtechinc.cruzr.intent.action.START_WAYGUIDE";
    @Override
    public void onReceive(final Context context, Intent intent) {
          String action=intent.getAction();
          if(INTENT_ACTION_START_WAYGUIDE.equals(action)){
              Log.e(TAG, "onReceive: 广播收到‘开始游览指令'->");
              Event event=new Event();
              event.source= Event.EVENT_CMD_NAME_START;
              NotificationCenter.defaultCenter().publish(event);

              WayGuiderPresenterImpl wg= (WayGuiderPresenterImpl) ServiceProxy.getPresenter(ServiceProxy.SERVICE_WAY_GUIDER);
              Log.e(TAG, "onReceive: wg==null"+(wg==null));

              if(wg!=null){

              }else {
                  new Timer().schedule(new TimerTask() {
                      @Override
                      public void run() {
                          context.sendBroadcast(new Intent("com.ubtechinc.cruzr.intent.action.START_WAYGUIDE"));
                      }
                  },1000);
              }
          }
    }
}
