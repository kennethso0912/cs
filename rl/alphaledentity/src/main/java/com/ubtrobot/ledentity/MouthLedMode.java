package com.ubtrobot.ledentity;

/**
 * @desc : 嘴巴灯效模式
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/8/24
 * @modifier:
 * @modify_time:
 */

public enum MouthLedMode {
    COMMON(0x00),BREATH(0x01);
    public final int value;
    MouthLedMode(int value) {
        this.value = value;
    }
    public static MouthLedMode valueOf(int value){
        switch (value){
            case 0x01:
                return BREATH;
            default:
                return COMMON;
        }
    }
}
