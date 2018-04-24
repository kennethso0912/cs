package com.ubtechinc.cruiser.wayguide.utils;

import com.ubtechinc.cruiser.wayguide.event.TtsEvent;
import com.ubtechinc.framework.notification.NotificationCenter;

/**
 * Created on 2017/11/10.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class SpeakUtil {
    private SpeakUtil(){

    }

    public static void speak(String tts) {
        TtsEvent event=new TtsEvent();
        event.in=TtsEvent.EVENT_MOTION_INTRO_SPEAK;
        event.source= tts;
        NotificationCenter.defaultCenter().publish(event);
    }

}
