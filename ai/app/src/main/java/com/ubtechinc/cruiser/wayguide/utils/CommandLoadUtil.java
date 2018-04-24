package com.ubtechinc.cruiser.wayguide.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.ubtechinc.cruiser.wayguide.model.CommandSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2017/7/17.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc 本地指令加载类
 */
public class CommandLoadUtil {
    private static final String TAG = "CommandLoadUtil";
    
    private static String cmdPath="conf/cmd_zh.js";

    static {

    }

    public static Map<String,String> parse(Context context){
        long starttime=System.currentTimeMillis();
        Map<String,String> cmdMap=new HashMap<>();
        String cmds=ParserUtil.parseFile1(context,cmdPath);

        CommandSet set=new Gson().fromJson(cmds,CommandSet.class);

        List<String> starts=set.getStarts();
        List<String> pauses=set.getPauses();
        List<String> continues=set.getContinues();
        List<String> ends=set.getEnds();
        List<String> intros=set.getIntros();

        for (String s:starts){
            cmdMap.put(s,"start");
        }

        for (String s:pauses){
            cmdMap.put(s,"pause");
        }

        for (String s:continues){
            cmdMap.put(s,"continue");
        }

        for (String s:ends){
            cmdMap.put(s,"end");
        }

        for (String s:intros){
            cmdMap.put(s,"intro");
        }
        long endtime=System.currentTimeMillis();
        long wasteTime=endtime-starttime;

        Log.e(TAG, "onCreate: waste_time:"+wasteTime);
        Log.e(TAG, "onCreate: cmdMap:"+cmdMap);
        return cmdMap;
    }
}
