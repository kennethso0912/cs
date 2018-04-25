
package com.ubtechinc.cruiser.wayguide.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.ubtechinc.cruiser.wayguide.R;
import com.ubtechinc.cruiser.wayguide.app.AppApplication;

import org.w3c.dom.Text;


/**
 * Created on 2017/10/31.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class SettingsDataUtil {
    private static final String SETTING_DATA_CP_URI = "content://com.ubtechinc.settings.provider/CruiserSettings";
    private static final String SETTING_DATA_TYPE_UNDERPAN_USABLE = "cruiser_chassis_motion_state";
    private static final String SETTING_DATA_TYPE_WAKEUP_WORD = "cruiser_wakeup_word";
    private final String[] projection = {"key", "value"};
    private final String selection = "key=?";
    private boolean underPanUsable;
    private String wakeupWord;

    private SettingsDataUtil() {

    }

    private static class HOLDER {
        private static SettingsDataUtil _instance = new SettingsDataUtil();
    }

    public static SettingsDataUtil get() {
        return HOLDER._instance;
    }

    public boolean getUnderPanCanUsable() {
        queryData(AppApplication.getContext(), SETTING_DATA_TYPE_UNDERPAN_USABLE);//in order to load just only one time
        return this.underPanUsable;
    }

    public String getWakeupWord(String defaultValue) {
        return TextUtils.isEmpty(this.wakeupWord) ? defaultValue : this.wakeupWord;
    }

    public void loadSettingsData(Context context) {
        queryData(context, SETTING_DATA_TYPE_UNDERPAN_USABLE);
//        queryData(context, SETTING_DATA_TYPE_WAKEUP_WORD);
    }

    public void queryData(Context context, String dataType) {

        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(SETTING_DATA_CP_URI);
        String[] selectionArgs = {dataType};

        Cursor result = null;

        try {
            result = cr.query(uri, projection, selection, selectionArgs, null);
            if (result != null) {
                while (result.moveToNext()) {
                    if (SETTING_DATA_TYPE_UNDERPAN_USABLE.equals(dataType)) {
                        this.underPanUsable = Boolean.valueOf(result.getString(result.getColumnIndex("value")));
                    } else if (SETTING_DATA_TYPE_WAKEUP_WORD.equals(dataType)) {
                        this.wakeupWord = result.getString(result.getColumnIndex("value"));
                    }
                }
            }
        } catch (Exception e) {
            Log.d("zjm for tst", "getAutoPwrSaveState query error");
            e.printStackTrace();
        } finally {
            if (result != null) {
                result.close();
            }
        }

    }
}

