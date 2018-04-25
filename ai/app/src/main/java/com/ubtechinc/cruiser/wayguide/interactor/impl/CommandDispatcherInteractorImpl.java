package com.ubtechinc.cruiser.wayguide.interactor.impl;

import android.content.Context;
import android.util.Log;

import com.ubtechinc.cruiser.wayguide.event.Event;
import com.ubtechinc.cruiser.wayguide.interactor.ICommandDispatcherInteractor;
import com.ubtechinc.cruzr.sdk.speech.ISpeechContext;
import com.ubtechinc.cruzr.sdk.speech.SpeechRobotApi;
import com.ubtechinc.cruzr.serverlibutil.interfaces.InitListener;
import com.ubtechinc.framework.log.NLog;
import com.ubtechinc.framework.notification.NotificationCenter;

import static com.ubtechinc.cruiser.wayguide.utils.UbtConstant.*;


/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 指令接收处理模型类
 */
public class CommandDispatcherInteractorImpl implements ICommandDispatcherInteractor {
    private static final String TAG = "CommandDispatcher";
    private Context mContext;
    private SpeechRobotApi mSpeechRobotApi;
    private final  static String WAY_GUIDE_START="开始游览";
    private final  static String WAY_GUIDE_PAUSE="暂停游览";
    private final  static String WAY_GUIDE_CONTINUE="继续游览";
    private final  static String WAY_GUIDE_END="结束游览";
    private static final String WAY_GUIDE_INTRO_AGAIN = "再介绍一遍";
    private long mContinueStartTime=System.currentTimeMillis();
    private long mPauseStartTime=System.currentTimeMillis();

    public CommandDispatcherInteractorImpl(Context context){
        this.mContext=context;
    }

    @Override
    public void regDispatcher() {
        mSpeechRobotApi= SpeechRobotApi.get().initializ(this.mContext, APP_ID,mInitListener);
        mSpeechRobotApi.registerSpeech(new ISpeechContext() {
            @Override
            public void onStart() {
                NLog.i(TAG,"游览指令分发进入前台->");
            }

            @Override
            public void onStop() {
                NLog.i(TAG,"游览指令分发退出后台->");
            }

            @Override
            public void onResult(String s) {
                NLog.i(TAG,"游览指令->"+s);
//                if(s.equals(WAY_GUIDE_START)||s.equals((WAY_GUIDE_END))) {
                    publishEvent(s);
//                }
            }

            @Override
            public void onPause() {
                NLog.i(TAG,"游览暂停->onPause()->out");

                //时间间隔控制，类似按钮的重复多次点击
                if(System.currentTimeMillis()-mContinueStartTime>=5*1000) {
                    NLog.i(TAG,"游览暂停->onPause()->in");
                    mPauseStartTime=System.currentTimeMillis();
                    publishEvent(WAY_GUIDE_PAUSE);
                }
            }

            @Override
            public void onResume() {
                NLog.i(TAG,"游览恢复->onResume()");
            }
        });
    }

    /**
     * 根据不同指令内容进行事件发送
     * @param s 指令内容
     */
    private void publishEvent(String s) {
        Event event=new Event();
        event.source=s;

        switch (s) {
            case WAY_GUIDE_START:
                  mContinueStartTime=System.currentTimeMillis();
                  event.in=Event.EVENT_MOTION_INTRO_START;
                break;
            case WAY_GUIDE_PAUSE:
                  event.in=Event.EVENT_MOTION_INTRO_PAUSE;
                break;
            case WAY_GUIDE_CONTINUE:
                  if(System.currentTimeMillis()-mPauseStartTime<3*1000){
                      Log.e(TAG, "继续游览事件: 暂停时间太短->WAY_GUIDE_CONTINUE");
                      return;
                  }
                  mContinueStartTime=System.currentTimeMillis();
                  event.in=Event.EVENT_MOTION_INTRO_CONTINUE;
                break;
            case WAY_GUIDE_END:
                  event.in=Event.EVENT_MOTION_INTRO_END;
                break;
            case WAY_GUIDE_INTRO_AGAIN:
                  event.in=Event.EVENT_MOTION_INTRO_AGAIN;
                break;
            default:
                return;

        }
        NotificationCenter.defaultCenter().publish(event);
    }

    private InitListener mInitListener=new InitListener() {
        @Override
        public void onInit() {
            Log.e(TAG, "onInit: 语音初始化成功->");
        }
    };
}
