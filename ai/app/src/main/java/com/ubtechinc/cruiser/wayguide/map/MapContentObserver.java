package com.ubtechinc.cruiser.wayguide.map;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

import static com.ubtechinc.cruiser.wayguide.utils.ParserUtil.stringToFloat;

/**
 * Created on 2017/3/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc 游览数据改变更新监听器
 */

public class MapContentObserver extends ContentObserver {
    private static final String TAG = "MapContentObserver";

    public MapContentObserver(Handler handler) {
        super(handler);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.e(TAG, "onChange("+selfChange+")");
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        Log.e(TAG, "onChange: "+selfChange+","+uri+")");
        //判断改变数据是否游览路径表
        if (selfChange) {
            Log.e(TAG, "onChange: "+uri.getPath());
        }
    }

    private void delMediaDataAndFile(){

    }

    /**
     * 获取当前地图当前路线的所有普通点
     * @param resolver
     */
    @SuppressWarnings("unchecked")
    private ArrayList  getPointsByMap(ContentResolver resolver) {
        ArrayList mapIds=new ArrayList();

        Uri ALL_POINT_URI = Uri.parse("content://com.ubtechinc.cruzr.map.routinepathProvider/routinepath");

        String[] wayGuideClomuns={"map_id"};

        Cursor cursor=resolver.query(ALL_POINT_URI,wayGuideClomuns,"0==0 group by mapmodel.id", null , null);
        if (cursor != null&&cursor.moveToFirst()) {
            while (!cursor.isAfterLast()){
                int map_id=cursor.getInt(cursor.getColumnIndexOrThrow("map_id"));
                mapIds.add(map_id);
                Log.e(TAG, "getPointsByMap-> map_id="+map_id);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return mapIds;
    }
}
