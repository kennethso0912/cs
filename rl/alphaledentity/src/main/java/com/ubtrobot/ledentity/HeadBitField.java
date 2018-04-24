package com.ubtrobot.ledentity;

/**
 * @desc :
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/8/24
 * @modifier:
 * @modify_time:
 */

public enum HeadBitField {
    BIT_1(Integer.valueOf("00000001",2)),
    BIT_11(Integer.valueOf("00000011",2)),
    BIT_111(Integer.valueOf("00000111",2)),
    BIT_1111(Integer.valueOf("00001111",2)),
    BIT_11111(Integer.valueOf("00011111",2));
    public final int bit;

    HeadBitField(int bitField){
        this.bit =  bitField;
    }

    public HeadBitField valueOf(int bit){
        switch (bit){
            case 0x01:
                return BIT_1;
            case 0x02:
                return BIT_11;
            case 0x03:
                return BIT_111;
            case 0x04:
                return BIT_1111;
            case 0x05:
                return BIT_11111;
            default:
                return BIT_1;
        }
    }
}
