package com.ubtechinc.cruiser.wayguide.task.impl;

import android.util.Log;

import com.ubtechinc.cruiser.wayguide.callback.ITaskCallback;
import com.ubtechinc.cruiser.wayguide.task.IActionTask;
import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created on 2017/8/22.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class ActionTaskImpl implements IActionTask{
    private static final String TAG = "action";
    private List<String> mActionList = new ArrayList<>();
    private static String randAction[] = {"talk1", "talk2", "talk3","talk4","talk5", "talk6", "talk7","talk8","talk9"};

    public ActionTaskImpl(){
        mActionList = Arrays.asList(randAction);
    }


    @Override
    public void play(String actName, ITaskCallback listener) {

    }

    @Override
    public void play() {
        Log.e(TAG, "play: 动作执行开始--->>>");
        RandomList r = new RandomList();
        Collections.sort(mActionList, r);
        int len = mActionList.size();
        String actions[] = new String[len];
        for (int i = 0; i < len; i++) {
            actions[i] = mActionList.get(i);
        }
        RosRobotApi.get().run(actions);
    }

    @Override
    public void stop() {
        Log.e(TAG, "play: 动作执行结束--->>>");
        RosRobotApi.get().run("reset");
    }

    class RandomList implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            int r = getRandom();
            if (r == 0) {
                return -1;
            } else if (r == 1) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private int getRandom() {
        Random rf = new Random();
        int r = rf.nextInt(2);
        return r;
    }
}
