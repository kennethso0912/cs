package com.ubtechinc.alphamini;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * @desc : 默认的嘴巴灯控制实现
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/13
 */

public class MouthLedController implements ILedController {

    private static final int MSG_STOP = 1;
    private final static String SPACE = " ";
    private static final int DEFAULT_BREATH_DURATION = 300;
    private static String breath_led_red = "/sys/class/leds/red/brightness";//"/sys/devices/soc/11009000.i2c/i2c-2/2-0064/leds/red/brightness";
    private static String breath_led_blue = "/sys/class/leds/blue/brightness";//"/sys/devices/soc/11009000.i2c/i2c-2/2-0064/leds/blue/brightness";
    private static String breath_led_green = "/sys/class/leds/green/brightness";//"/sys/devices/soc/11009000.i2c/i2c-2/2-0064/leds/green/brightness";
    private static String breath_led_red_time = "/sys/class/leds/red/led_time";
    private static String breath_led_blue_time = "/sys/class/leds/blue/led_time";
    private static String breath_led_green_time = "/sys/class/leds/green/led_time";
    private static String breath_led_red_blink = "/sys/class/leds/red/blink";
    private static String breath_led_blue_blink  = "/sys/class/leds/blue/blink";
    private static String breath_led_green_blink  = "/sys/class/leds/green/blink";
    private static String breath_led_current = "/sys/class/leds/blue/rgbcurrent";

    final String breath_time = "2 2 2 2";
    final String breath_time_low = "3 3 3 3";
    final String breath_time_high = "14 14 14 14";
    final byte[] color_dark = {'0'};
    final byte[] color_light = {'1'};
    final byte[] color_white = {'2', '5', '5'};
    final byte[] color_default = color_white;

    private SparseArray<Float> sparseArray = new SparseArray<Float>(16) {
        private Float[] values = new Float[]{0f, 0.13f, 0.26f, 0.38f, 0.51f, 0.77f, 1.04f, 1.6f, 2.1f, 3.1f, 4.2f, 5.2f, 6.2f, 7.3f, 8.3f};
        {
            int count = values.length;
            for(int i = 0; i < count; i ++) {
                put(i, values[i]);
            }
        }

    };

    private static Object lock = new Object();
    private static volatile MouthLedController instance;
    private static ColorRGB colorRGB;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_STOP:
                    turnOff();
                    break;
                default:
                    break;
            }
        }
    };

    private MouthLedController(){}

    public static MouthLedController getInstance() {
        if(instance == null) {
            synchronized (lock) {
                if(instance == null) {
                    instance = new MouthLedController();
                }
            }
        }
        return instance;
    }
    @Override
    public void turnOn() {
        setcolor(1, color_default);
        setcolor(2, color_default);
        setcolor(3, color_default);
    }

    @Override
    public void turnOff() {
        setColorBlink(Color.COLOR_RED, true);
        setColorBlink(Color.COLOR_GREEN, true);
        setColorBlink(Color.COLOR_BLUE, true);
    }

    @Override
    public void startNormalModel(int color, int duration) {
        colorRGB = ColorRGB.newInstance(color);
        startNormalModel(colorRGB);
        postDelayResume(duration);
    }

    private void startNormalModel(ColorRGB colorRGB) {
        handler.removeMessages(MSG_STOP);
        setcolor(0, colorRGB.getbRed());
        setcolor(1, colorRGB.getbGreen());
        setcolor(2, colorRGB.getbBlue());
    }

    private void resumeNormal() {
        startNormalModel(colorRGB);
    }

    private void postDelayResume(int delay) {
        if(delay != -1) {
            handler.removeMessages(MSG_STOP);
            handler.sendEmptyMessageDelayed(MSG_STOP, delay);
        }
    }

    @Override
    public void startBreathModel(int color, int duration) {
        ColorRGB colorRGB = ColorRGB.newInstance(color);
        ColorRGBLite colorRGBLite = ColorRGBLite.valueOf(colorRGB);
        setColorBlink(Color.COLOR_RED, colorRGBLite.red == 0);
        setColorBlink(Color.COLOR_GREEN, colorRGBLite.green == 0);
        setColorBlink(Color.COLOR_BLUE, colorRGBLite.blue == 0);
        setCurrentColor(colorRGBLite);
        setLedTime(getTimeDes(DEFAULT_BREATH_DURATION, DEFAULT_BREATH_DURATION, DEFAULT_BREATH_DURATION, DEFAULT_BREATH_DURATION), colorRGBLite);
        postDelayResume(duration);
    }

    @Override
    public void startBreathModel(int color, int breathDuration, int duration) {
        ColorRGB colorRGB = ColorRGB.newInstance(color);
        ColorRGBLite colorRGBLite = ColorRGBLite.valueOf(colorRGB);
        setColorBlink(Color.COLOR_RED, colorRGBLite.red == 0);
        setColorBlink(Color.COLOR_GREEN, colorRGBLite.green == 0);
        setColorBlink(Color.COLOR_BLUE, colorRGBLite.blue == 0);
        setCurrentColor(colorRGBLite);
        setLedTime(getTimeDes(breathDuration, breathDuration, breathDuration, breathDuration), colorRGBLite);
        postDelayResume(duration);
    }

    private void setLedTime(String time, ColorRGBLite colorRGBLite) {

        FileOutputStream fos_time = null;
        try {
            if(colorRGBLite.red != 0) {
                fos_time = new FileOutputStream(breath_led_red_time);
                fos_time.write(time.getBytes());
                fos_time.close();
                Log.d("1124", " breath_led_red_time -- time : " + time);
            }
            if(colorRGBLite.green != 0) {
                fos_time = new FileOutputStream(breath_led_green_time);
                fos_time.write(time.getBytes());
                fos_time.close();
                Log.d("1124", " breath_led_green_time -- time : " + time);
            }
            if(colorRGBLite.blue != 0) {
                fos_time = new FileOutputStream(breath_led_blue_time);
                fos_time.write(time.getBytes());
                fos_time.close();
                Log.d("1124", " breath_led_blue_time -- time : " + time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setCurrentColor(ColorRGBLite colorRGBLite) {
        String value = colorRGBLite.toColorDes();
        Log.d("1124", " setCurrentColor -- value : " + value);
        FileOutputStream fos_current = null;
        try {
            fos_current = new FileOutputStream(breath_led_current);
            fos_current.write(value.getBytes());
            fos_current.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setcolor(int rgb,byte[] color_value){
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
            fos.write(color_value);
            fos.close();
            Log.d("TEST","set success!"+"  "+node_color);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TEST","set exception!"+"  "+node_color);
        }
    }

    private void setColorTime(Color color){
        FileOutputStream fos_time = null;
        String node_color_time = null;
        switch(color){
            case COLOR_RED:
                node_color_time = breath_led_red_time;
                break;
            case COLOR_GREEN:
                node_color_time = breath_led_green_time;
                break;
            case COLOR_BLUE:
                node_color_time = breath_led_blue_time;
                break;
        }
        if(node_color_time != null) {
            try {
                fos_time = new FileOutputStream(node_color_time);
                fos_time.write(breath_time.getBytes());
                fos_time.close();
                Log.d("TEST", "set led_time success! " + "  " + node_color_time);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("TEST", "set led_time exception!" + "  " + node_color_time);
            }
        }
    }

    private void setColorBlink(Color color, boolean isBlink){
        Log.d("1124", " setColorBlink : " + color + " isBlink : " + isBlink);
        FileOutputStream fos_blink = null;
        String node_color_blink = null;
        switch(color){
            case COLOR_RED:
                node_color_blink = breath_led_red_blink;
                break;
            case COLOR_GREEN:
                node_color_blink = breath_led_green_blink;
                break;
            case COLOR_BLUE:
                node_color_blink = breath_led_blue_blink;
                break;

        }
        if(node_color_blink != null) {
            try {
                fos_blink = new FileOutputStream(node_color_blink);
                if(isBlink) {
                    fos_blink.write(color_dark);
                } else {
                    fos_blink.write(color_light);
                }
                fos_blink.close();
                Log.d("TEST", "set led_blink success! " + "  ");
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("TEST", "set led_blink exception!" + "  ");
            }
        }
    }

    private void setLedBreathtimes(){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(breath_led_red_time);
            fos.write(breath_time.getBytes());
            fos.close();

            Log.d("TEST","set led time success!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TEST","set led time exception!");
        }
    }

    /**
     *  颜色值以0-15表示
     */
    private static class ColorRGBLite {

        private final static float TRANSFOR_PARAM = 15f / 255f;
        private int red;
        private int green;
        private int blue;

        private static ColorRGBLite valueOf(ColorRGB colorRGB) {
            return new ColorRGBLite(colorRGB);
        }

        private ColorRGBLite(ColorRGB colorRGB) {
            this.red = (int)(colorRGB.red * TRANSFOR_PARAM);
            this.green = (int)(colorRGB.green * TRANSFOR_PARAM);
            this.blue = (int)(colorRGB.blue * TRANSFOR_PARAM);
        }

        // 返回颜色描述符
        public String toColorDes() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(red);
            stringBuilder.append(SPACE);
            stringBuilder.append(green);
            stringBuilder.append(SPACE);
            stringBuilder.append(blue);
            return stringBuilder.toString();
        }

        @Override
        public String toString() {
            return toColorDes();
        }
    }

    private static class ColorRGB {
        private static final int START_NUM = 48;
        private int red;
        private int green;
        private int blue;
        private byte[] bRed;
        private byte[] bGreen;
        private byte[] bBlue;

        static ColorRGB newInstance(int color) {
            return new ColorRGB(color);
        }

        private ColorRGB(int color) {
            red = (color & 0xff0000) >> 16;
            green = (color & 0x00ff00) >> 8;
            blue = (color & 0x0000ff);
            bRed = getByteByInt(red);
            bGreen = getByteByInt(green);
            bBlue = getByteByInt(blue);
        }

        public byte[] getByteByInt(int value) {
            return new byte[] {(byte)(value % 1000 / 100 + START_NUM), (byte)(value %100 / 10 + START_NUM), (byte)(value % 10 + START_NUM)};
        }

        public byte[] getbRed() {
            return bRed;
        }

        public byte[] getbGreen() {
            return bGreen;
        }

        public byte[] getbBlue() {
            return bBlue;
        }

        @Override
        public String toString() {
            return "ColorRGB{" +
                    "red=" + red +
                    ", green=" + green +
                    ", blue=" + blue +
                    ", bRed=" + Arrays.toString(bRed) +
                    ", bGreen=" + Arrays.toString(bGreen) +
                    ", bBlue=" + Arrays.toString(bBlue) +
                    '}';
        }
    }

    private String getTimeDes(int blindToLight, int lightDuration, int lightToBlind, int gap) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getTimeSymbolFromInt(blindToLight));
        stringBuilder.append(SPACE);
        stringBuilder.append(getTimeSymbolFromInt(lightDuration));
        stringBuilder.append(SPACE);
        stringBuilder.append(getTimeSymbolFromInt(lightToBlind));
        stringBuilder.append(SPACE);
        stringBuilder.append(getTimeSymbolFromInt(gap));
        stringBuilder.append(SPACE);
        return stringBuilder.toString();
    }

    private int getTimeSymbolFromInt(int time) {
        Log.d("1124", " getTimeSymbolFromInt : " + (float)time / 1000);
        return getTimeSymbol((float)time / 1000);
    }

    private int getTimeSymbol(float time) {
        int count = sparseArray.size();
        for(int i = 0; i < count; i ++) {
            if(i == 0) {
                if(time == 0) {
                    return 0;
                }
            }
            else if(sparseArray.get(i) == time) {
                return i;
            } else if(sparseArray.get(i) > time
                    && sparseArray.get(i - 1) < time) {
                float sub1 = sparseArray.get(i) - time;
                float sub2 = time - sparseArray.get(i - 1);
                return sub1 >= sub2 ? i : i - 1;
            }
        }
        return count;
    }

    private enum Color{
        COLOR_RED, COLOR_GREEN, COLOR_BLUE,
    }
}
