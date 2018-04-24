package com.ubtechinc.testalphamini;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ubtrobot.lib.mouthledapi.MouthLedApi;

public class LedTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_test);
    }

    public void onSetOff(View view){
        MouthLedApi.get().turnOff();
    }

    public void onBreath(View view) {

        MouthLedApi.get().startBreathModel(getColor(), getBreathDuration(), getDuration());
    }

    public void onNormal(View view) {
        MouthLedApi.get().startNormalModel(getColor(), getDuration());
    }

    public void onTurnOn(View view) {
        MouthLedApi.get().turnOn();
    }

    private int getColor() {
        EditText editTextRed = (EditText)findViewById(R.id.et_red);
        EditText editTextGreen = (EditText)findViewById(R.id.et_green);
        EditText editTextBlue = (EditText)findViewById(R.id.et_blue);
        int red = 255;
        int green = 255;
        int blue = 255;
        if(editTextRed.getText() != null && !editTextRed.getText().toString().equals("")) {
            try {
                red = Integer.valueOf(editTextRed.getText().toString());
            } catch (Exception e) {
                Toast.makeText(this, "红色值设置有误", Toast.LENGTH_SHORT).show();
            }
        }
        if(editTextGreen.getText() != null && !editTextGreen.getText().toString().equals("")) {
            try {
                green = Integer.valueOf(editTextGreen.getText().toString());
            } catch (Exception e) {
                Toast.makeText(this, "绿色值设置有误", Toast.LENGTH_SHORT).show();
            }
        }
        if(editTextBlue.getText() != null && !editTextBlue.getText().toString().equals("")) {
            try {
                blue = Integer.valueOf(editTextBlue.getText().toString());
            } catch (Exception e) {
                Toast.makeText(this, "蓝色值设置有误", Toast.LENGTH_SHORT).show();
            }
        }
        return Color.argb(0, red, green, blue);
    }

    private int getDuration() {
        EditText editTextDuration = (EditText)findViewById(R.id.et_duration);
        if(editTextDuration.getText() != null && !editTextDuration.getText().toString().equals("")) {
            try {
                return Integer.valueOf(editTextDuration.getText().toString());
            } catch (Exception e) {
                Toast.makeText(this, "总时间格式错误", Toast.LENGTH_SHORT).show();
            }
        }
        return 2000;
    }

    private int getBreathDuration() {
        EditText editTextDuration = (EditText)findViewById(R.id.et_breaduration);
        if(editTextDuration.getText() != null && !editTextDuration.getText().toString().equals("")) {
            try {
                return Integer.valueOf(editTextDuration.getText().toString());
            } catch (Exception e) {
                Toast.makeText(this, "呼吸时间格式错误", Toast.LENGTH_SHORT).show();
            }
        }
        return 2000;
    }
}
