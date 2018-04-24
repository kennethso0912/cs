package com.ubtechinc.testalphamini;

import java.io.FileOutputStream;

/**
 * @desc :
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/24
 */

public class ledUtils {
    private static String node_spi = "/sys/class/leds/lcd-backlight/brightness";
    private static String nodemax_spi = "/sys/class/leds/lcd-backlight/max_brightness";
    private static String breath_led_red = "/sys/class/leds/red/brightness"; // 红灯亮度
    private static String breath_led_blue = "/sys/class/leds/blue/brightness";//"/sys/devices/soc/11009000.i2c/i2c-2/2-0064/leds/blue/brightness";
    private static String breath_led_green = "/sys/class/leds/green/brightness";//"/sys/devices/soc/11009000.i2c/i2c-2/2-0064/leds/green/brightness";
    private static String breath_led_red_time = "/sys/class/leds/red/led_time";
    private static String breath_led_blue_time = "/sys/class/leds/blue/led_time";
    private static String breath_led_green_time = "/sys/class/leds/green/led_time";

    private static String breath_led_red_blink = "/sys/class/leds/red/blink";
    private static String breath_led_blue_blink  = "/sys/class/leds/blue/blink";
    private static String breath_led_green_blink  = "/sys/class/leds/green/blink";

    private static String breath_led_current = "/sys/class/leds/red/rgbcurrent";

    void turnOn(){
        final byte[] color= {'2','5','5'};//对应的亮度是255 写入节点都要把int转换成byte数组
        setBrightness(0,color);   //对应写入红色亮度节点255
        setBrightness(1,color);   //对应写入绿色亮度节点255
        setBrightness(2,color);   //对应写入蓝色亮度节点255
    }

    void startNormalModel(int color,int light){
        //设置的颜色需要分别对三种颜色的brightness节点进行写入
        //如果要设置紫色   #FF00FF
        final byte[] color_red= {'2','5','5'};
        final byte[] color_green= {'0'};
        final byte[] color_blue= {'2','5','5'};

        setBrightness(0,color_red);   //对应写入红色亮度节点255
        setBrightness(1,color_green);   //对应写入绿色亮度节点0
        setBrightness(2,color_blue);   //对应写入蓝色亮度节点255
    }

    void turnOff(){
        setColorBlink(0);  //对应把红色灯节点的blink节点设置成0
        setColorBlink(1);  //对应把绿色灯节点的blink节点设置成0
        setColorBlink(2);  //对应把蓝色灯节点的blink节点设置成0
    }

    void startBreathModel(int color,int light,long period,long gap, int count){
        //比如要设置紫色呼吸   #FF00FF   先要把对应rgb的blink设置成1  如果是0 对应的blink就设置为0
        FileOutputStream fos = null;
        final byte[] open_blink= {'1'};
        final byte[] close_blink= {'0'};
        final String breath_color = "15 0 15";

        try {
            fos = new FileOutputStream(breath_led_red_blink);  //对应红色blink节点设置成1
            fos.write(open_blink);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            fos = new FileOutputStream(breath_led_green_blink);  //对应绿色blink节点设置成0
            fos.write(close_blink);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            fos = new FileOutputStream(breath_led_blue_blink);  //对应蓝色blink节点设置成1
            fos.write(open_blink);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //进行设置颜色对current节点写入格式为"15 15 15" 分别代表对应的rgb设置的颜色  15对应的是255按照比例计算
        FileOutputStream fos_current = null;
        try {
            fos_current = new FileOutputStream(breath_led_current);
            fos_current.write(breath_color.getBytes());
            fos_current.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //设置呼吸一次的时间长和每次呼吸的间隔   默认为"6 0 6 4" 每一位的参数代表时间长而且每一位的参数范围0~15  d第一位代表灭到亮的时间 第二位代表常亮的时间 第三位代表从亮到灭的时间 最后一位代表每次呼吸的间隔时间
        //0~15对应的时间长
        /* 0 -----  0s
           1 ------  0.13s
           2 ------  0.26s
           3 -------   0.38s
           4 -------   0.51s
           5 --------   0.77s
           6  -------   1.04s
           7 ---------   1.6s
           8 ---------     2.1s
           9 ---------     2.6s
           10  --------     3.1s
           11 ----------     4.2s
           12  ---------     5.2s
           13  ---------     6.2s
           14 -----------    7.3s
           15  ----------     8.3s
          */
        FileOutputStream fos_time = null;
        String time="6 0 6 4";
        try {
            fos_time = new FileOutputStream(breath_led_red_time);
            fos_time.write(time.getBytes());
            fos_time.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*try {
            fos_time = new FileOutputStream(breath_led_green_time);    //由于green的颜色是0所以不用设置ledtime
            fos_time.write(breath_color.getBytes());
            fos_time.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        try {
            fos_time = new FileOutputStream(breath_led_blue_time);
            fos_time.write(time.getBytes());
            fos_time.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBrightness(int rgb,byte[] light){
        FileOutputStream fos = null;

        String node_color = null;

        switch(rgb){
            case 0:
                node_color = breath_led_red;
                break;
            case 1:
                node_color = breath_led_green;
                break;
            case 2:
                node_color = breath_led_blue;
                break;
        }
        try {
            fos = new FileOutputStream(node_color);
            fos.write(light);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setColorBlink(int rgb){

        FileOutputStream fos_blink = null;
        final byte[] color3= {'0'};   //对应的是亮度0也就是关

        String node_color_blink = null;
        switch(rgb){
            case 0:

                node_color_blink = breath_led_red_blink;
                break;
            case 1:

                node_color_blink = breath_led_green_blink;
                break;
            case 2:

                node_color_blink = breath_led_blue_blink;
                break;

        }
        //分别往不同的颜色的blink节点写入0
        if(node_color_blink != null) {
            try {
                fos_blink = new FileOutputStream(node_color_blink);
                fos_blink.write(color3);
                fos_blink.close();

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

}
