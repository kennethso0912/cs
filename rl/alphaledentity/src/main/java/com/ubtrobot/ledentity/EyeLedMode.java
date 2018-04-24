package com.ubtrobot.ledentity;

/**
 * @desc : 眼睛灯效模式
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/8/24
 * @modifier:
 * @modify_time:
 */

public enum EyeLedMode {
    COMMON(0x00),MARQUEE(0x01),FLASH(0x03),LIGHT(0x05),BLINK(0x07);
    public final int value;
    EyeLedMode(int value) {
        this.value = value;
    }
    public static EyeLedMode valueOf(int value){
        switch (value){
            case 0x01:
                return MARQUEE;
            case 0x03:
                return FLASH;
            case 0x05:
                return LIGHT;
            case 0x07:
                return BLINK;
            default:
                return COMMON;
        }
    }
}
