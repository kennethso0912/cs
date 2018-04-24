package com.ubtrobot.ledentity;

/**
 * @desc :
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/8/24
 * @modifier:
 * @modify_time:
 */

public enum EyeBitField {
    BIT_1(Integer.valueOf("00000001",2)),
    BIT_11(Integer.valueOf("00000011",2)),
    BIT_111(Integer.valueOf("00000111",2)),
    BIT_1111(Integer.valueOf("00001111",2)),
    BIT_11111(Integer.valueOf("00011111",2)),
    BIT_111111(Integer.valueOf("00111111",2)),
    BIT_1111111(Integer.valueOf("01111111",2)),
    BIT_11111111(Integer.valueOf("11111111",2));
    public final int bit;

    EyeBitField(int bitField){
       this.bit =  bitField;
    }

    public EyeBitField valueOf(int bit){
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
            case 0x06:
                return BIT_111111;
            case 0x07:
                return BIT_1111111;
            case 0x08:
                return BIT_11111111;
            default:
                return BIT_1;
        }
    }
}
