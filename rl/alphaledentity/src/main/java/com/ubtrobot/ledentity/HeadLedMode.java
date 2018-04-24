package com.ubtrobot.ledentity;

/**
 * @desc : 头灯模式
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/8/24
 * @modifier:
 * @modify_time:
 */

public enum HeadLedMode {
    COMMON(0x00),BREATH(0x01),MARQUEE(0x03),FLASH(0x05),LIGHT(0x07);
    public final int value;
    HeadLedMode(int value) {
        this.value = value;
    }
    public static HeadLedMode valueOf(int value){
        switch (value){
            case 0x01:
                return BREATH;
            case 0x03:
                return MARQUEE;
            case 0x05:
                return FLASH;
            case 0x07:
                return LIGHT;
            default:
                return COMMON;
        }
    }
}
