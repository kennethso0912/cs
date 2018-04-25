package com.ubtechinc.cruiser.wayguide.interactor.impl;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.ubtechinc.cruiser.wayguide.callback.ITtsTaskCallback;
import com.ubtechinc.cruiser.wayguide.task.impl.SayTaskImpl;
import com.ubtechinc.cruiser.wayguide.ui.MainActivity;
import com.ubtechinc.cruiser.wayguide.R;
import com.ubtechinc.cruiser.wayguide.event.Event;
import com.ubtechinc.cruiser.wayguide.interactor.ICommandDispatcherInteractor;
import com.ubtechinc.cruiser.wayguide.model.CommandSet;
import com.ubtechinc.cruiser.wayguide.utils.ParserUtil;
import com.ubtechinc.cruzr.sdk.entity.Nlu;
import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;
import com.ubtechinc.cruzr.sdk.speech.ISpeechContext;
import com.ubtechinc.cruzr.sdk.speech.SpeechConstant;
import com.ubtechinc.cruzr.sdk.speech.SpeechRobotApi;
import com.ubtechinc.cruzr.serverlibutil.interfaces.InitListener;
import com.ubtechinc.cruzr.serverlibutil.interfaces.SpeechASRListener;
import com.ubtechinc.framework.log.NLog;
import com.ubtechinc.framework.notification.NotificationCenter;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.ubtechinc.cruiser.wayguide.utils.UbtConstant.APP_ID;


/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 指令接收处理模型类
 */
public class CommandNewDispatcherInteractorImpl implements ICommandDispatcherInteractor {
    private static final String TAG = "CommandDispatcher";
    private Context mContext;
    private SpeechRobotApi mSpeechRobotApi;
    private final  static String WAY_GUIDE_START="start";
    private final  static String WAY_GUIDE_PAUSE="pause";
    private final  static String WAY_GUIDE_CONTINUE="continu";
    private final  static String WAY_GUIDE_END="end";
    private static final String WAY_GUIDE_INTRO_AGAIN = "intro";
    private long mContinueStartTime=System.currentTimeMillis();
    private long mPauseStartTime=System.currentTimeMillis();

    private static final String VIEW_DOWNLOAD_PROGRESS = "viewdownload";


    public CommandNewDispatcherInteractorImpl(Context context){
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
                String cmd= Nlu.getIntent(s);
                int wakeupType=Nlu.getNlu(s).wakeup;
                NLog.i(TAG,"游览指令->wakeupType="+wakeupType);
                publishEvent(cmd,wakeupType);
            }

            @Override
            public void onPause() {
                NLog.i(TAG,"游览暂停->onPause()->out");

                //时间间隔控制，类似按钮的重复多次点击
                /**暂时注释 BYD版本的不能被其它互斥任务(闲聊不是互斥任务)指令暂停，要开始其它互斥任务，必须先结束游览*/
//                if(System.currentTimeMillis()-mContinueStartTime>=5*1000) {
//                    NLog.i(TAG,"游览暂停->onPause()->in");
//                    mPauseStartTime=System.currentTimeMillis();
//                    publishEvent(WAY_GUIDE_PAUSE);
//                }
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
    private void publishEvent(String s,int wakeupType) {
        if(s.isEmpty()){
             Log.e(TAG, "publishEvent  指令为空->");
            return;
        }

//        String cmd="";
//        if(WAY_GUIDE_PAUSE.equals(s)){
//            cmd=s;
//        }else {
//            cmd = cmdMap.get(s);
//        }

       String cmd = cmdMap.get(s);
        Log.e(TAG, "publishEvent: s="+s+",cmd="+cmd);

        if(cmd==null||cmd.isEmpty()){
              Log.e(TAG, "publishEvent: 解析后的游览本地意图指令 cmd 为空");
            return;
        }

        if(cmd.equals(Event.EVENT_CMD_NAME_VIEW_DOWNLOAD)){
            Log.e(TAG, "publishEvent: 查看媒体下载进度->");
            Intent intent=new Intent(mContext, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            return;
        }

        int askId=getAskConfirmTip(cmd,wakeupType);

        if(askId==0) {
            sendEvent(cmd);
        }else {
            doAskConfirm(askId,cmd);
        }
    }

    private void sendEvent(String cmd) {
        Event event=new Event();
        event.source=cmd;
        NotificationCenter.defaultCenter().publish(event);
    }

    private InitListener mInitListener=new InitListener() {
        @Override
        public void onInit() {
            Log.e(TAG, "onInit: 语音初始化成功->");
        }
    };

    private static  Map<String,String> cmdMap=new HashMap<>();

    public static void parseLocalCmd(Context context){
        long startTime=System.currentTimeMillis();

        String lan=Locale.getDefault().getLanguage();

        Log.e(TAG, "parseLocalCmd: lan="+lan);
        String cmds= ParserUtil.parseFile1(context,(context.getString(R.string.cmd_path,lan)));
        if(lan.equalsIgnoreCase("zh")){
            String place=Locale.getDefault().getCountry().toLowerCase();
            cmds= ParserUtil.parseFile1(context,(context.getString(R.string.cmd_path,lan+"_"+place)));
        }

        CommandSet set=new Gson().fromJson(cmds,CommandSet.class);

        List<String> starts=set.getStarts();
        List<String> pauses=set.getPauses();
        List<String> continues=set.getContinues();
        List<String> ends=set.getEnds();
        List<String> intros=set.getIntros();

        List<String> viewdownload=set.getViewdownload();

        for (String s:starts){
            cmdMap.put(s.trim(),Event.EVENT_CMD_NAME_START);
        }

        for (String s:pauses){
            cmdMap.put(s.trim(),Event.EVENT_CMD_NAME_PAUSE);
        }

        for (String s:continues){
            cmdMap.put(s.trim(),Event.EVENT_CMD_NAME_CONTINUE);
        }

        for (String s:ends){
            cmdMap.put(s.trim(),Event.EVENT_CMD_NAME_END);
        }

        for (String s:intros){
            cmdMap.put(s.trim(),Event.EVENT_CMD_NAME_INTRO_AGAIN);
        }

        for (String s:viewdownload){
            cmdMap.put(s.trim(),Event.EVENT_CMD_NAME_VIEW_DOWNLOAD);
        }

        long endTime=System.currentTimeMillis();
        long wasteTime=endTime-startTime;

        Log.e(TAG, "onCreate:parseLocalCmd: 解析本地指令成功->>> waste_time:"+wasteTime+";cmdMap="+cmdMap);

    }

    private int getAskConfirmTip(String cmd,int wakeupType){
          int askRid=0;

          if(wakeupType!= SpeechConstant.WAKE_UP_TYPE_FACE_IN){
              return askRid;
          }

          switch (cmd){
              case Event.EVENT_CMD_NAME_START:
                    askRid=R.string.ask_start;
                  break;
//              case Event.EVENT_CMD_NAME_CONTINUE:
//                    askRid=R.string.ask_contiune;
//                  break;
              default:
                  break;
          }

          return askRid;



    }

    private void doAskConfirm(int askRid,final String cmd){
        new SayTaskImpl().play(mContext.getString(askRid), new ITtsTaskCallback() {
            @Override
            public void onFinish(int code) {
                startQuery(cmd);
            }
        });
    }

    private void startQuery(final String cmd) {
        SpeechRobotApi.get().startSpeechASR(APP_ID, new SpeechASRListener() {
            @Override
            public void onBegin() {

            }

            @Override
            public void onEnd() {

            }

            @Override
            public void onVolumeChanged(int i) {

            }

            @Override
            public void onResult(String text, boolean isLast) {
                String yes = mContext.getString(R.string.tips_asr_yes);

                if (TextUtils.isEmpty(text)) {
                    return;
                }
                if (yes.equalsIgnoreCase(text)) {
                       sendEvent(cmd);
                       SpeechRobotApi.get().stopSpeechASR();
                    return;
                }

            }

            @Override
            public void onError(int code) {
            }

            @Override
            public void onIllegal() {

            }
        });
    }



}
