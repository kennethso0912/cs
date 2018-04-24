package com.ubtechinc.cruiser.wayguide.map;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import com.ubtechinc.cruiser.wayguide.exception.NoMapException;
import com.ubtechinc.cruiser.wayguide.exception.NoPathException;
import com.ubtechinc.cruiser.wayguide.R;
import com.ubtechinc.cruiser.wayguide.callback.IDataLoadCallback;
import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;
import java.util.ArrayList;

import static com.ubtechinc.cruiser.wayguide.utils.ParserUtil.*;


/**
 * Created on 2017/3/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 从地图获取map数据库游览数据
 */

public class MapDBProvider implements IMapProvider {
    private static final String TAG = "MapDBProvider";
    private static final String POINT_SEPERATOR=",";
    //游览顺序列表
    static final Uri WAYGUIDER_PATH_URI = Uri.parse("content://com.ubtechinc.cruzr.map.travelpathProvider/travelpath");
    //所有点的表
    private static final Uri ALL_POINT_URI = Uri.parse("content://com.ubtechinc.cruzr.map.positionProvider/position");

    private MapContentObserver mapContentObserver=new MapContentObserver(new Handler());
    private String curMapPath;
    private Context mContext;
    private ContentResolver mResolver;

    private String[] wayGuideClomuns={MapClomunsContants.NAME,MapClomunsContants.X,MapClomunsContants.Y,MapClomunsContants.CONTENT};
    private String[] pathClomuns={MapClomunsContants.PATH_POINTS};
    private String[] pointsInPath;

    public MapDBProvider(Context context){
         this.mContext=context;
         this.mResolver = this.mContext.getContentResolver();

         //监听游览数据变化
         this.mResolver.registerContentObserver(WAYGUIDER_PATH_URI,true,mapContentObserver);
         this.mResolver.registerContentObserver(ALL_POINT_URI,true,mapContentObserver);
    }

    @Override
    public MapInfo get() {
        return null;
    }

    @Override
    public MapInfo get(IDataLoadCallback cb) {
        long startTime=System.currentTimeMillis();

        MapInfo mapInfo;

        curMapPath = RosRobotApi.get().getCurrentMap();
        Log.e(TAG, "curMapPath->"+curMapPath);

        //ROS获取不了地图
        if (TextUtils.isEmpty(curMapPath)) {
              Log.e(TAG, "get: ->获取地图失败");
              cb.onFail(getString(R.string.no_map));
            return null;
        }

        //根据地图路径抽取地图名
        String curMapName= extractMapNameFromPath(curMapPath);
        Log.e(TAG, "get: curMap->"+curMapName);

        //获得所有游览点的路径
        pointsInPath = getWayGuiderPaths(mResolver,curMapName);

        //PC还未设置游览路线
        if(pointsInPath==null){
              cb.onFail(getString(R.string.no_wayguide_path));
            return null;
        }

        //获取无序的游览点集合
        ArrayList<MapInfo.Location> locs = getUnSortWGPoints(mResolver,curMapName);
        Log.e(TAG, "get: fetch wayguider data->"+locs.toString());

        //排序后的游览点集合
        ArrayList<MapInfo.Location> curLocs = sortWayGuiderPointByPath(pointsInPath, locs);

        mapInfo = new MapInfo();
        if (!curLocs.isEmpty()) {
            Log.e(TAG, "get: curLocs->"+curLocs.toString());
            mapInfo.location=curLocs;
        }

        long endTime=System.currentTimeMillis();
        Log.e(TAG, "get mapInfo: costTime->"+(endTime-startTime));

        return mapInfo;
    }

    @Override
    public void checkData() throws NoMapException,NoPathException{
          if(TextUtils.isEmpty(curMapPath)){
              throw new NoMapException("fail to get map ");
          }else if(pointsInPath==null){
              throw new NoPathException("no any wayguide path");
          }
    }


    /**
     * 根据游览路径进行游览点的排序
     *
     * @param pointsInPath
     * @param locs
     * @return
     */
    private ArrayList<MapInfo.Location> sortWayGuiderPointByPath(String[] pointsInPath, ArrayList<MapInfo.Location> locs) {
        ArrayList<MapInfo.Location> curLocs = new ArrayList<>();

        for (String pointName:pointsInPath) {
            for (MapInfo.Location loc:locs) {
                if(loc.getName().equals(pointName)){
                     curLocs.add(loc);
                }
            }
        }
        return curLocs;
    }

    /**
     * 获取无序的游览点
     * @param resolver
     */
    private ArrayList<MapInfo.Location> getUnSortWGPoints(ContentResolver resolver,String curMap) {
        ArrayList<MapInfo.Location> locs = new ArrayList<>();
        MapInfo.Location loc;
        String[] args={"travel_position",curMap};

        Cursor cursor=resolver.query(ALL_POINT_URI,wayGuideClomuns, MapClomunsContants.POINT_TYPE+" = ? and "+MapClomunsContants.Map_NAME+"= ?",args,null);
        if (cursor != null&&cursor.moveToFirst()) {
              while (!cursor.isAfterLast()){
                  loc=new MapInfo.Location();
                  loc.setName(cursor.getString(cursor.getColumnIndexOrThrow(MapClomunsContants.NAME)));
                  loc.setX(stringToFloat(cursor.getString(cursor.getColumnIndexOrThrow(MapClomunsContants.X))));
                  loc.setY(stringToFloat(cursor.getString(cursor.getColumnIndexOrThrow(MapClomunsContants.Y))));
                  loc.setContent(cursor.getString(cursor.getColumnIndexOrThrow(MapClomunsContants.CONTENT)));

                  locs.add(loc);

                  cursor.moveToNext();
              }
              cursor.close();
        }
        return locs;
    }

    /**
     * 获取已排序的游览路径
     *
     * @param resolver
     * @return
     */
    private String[] getWayGuiderPaths(ContentResolver resolver,String mapName) {
        String[] arrPaths = null;
        String paths=null;
        String[] args={mapName};

        Cursor pathCursor = resolver.query(WAYGUIDER_PATH_URI,pathClomuns,"map_name = ?",args,null);
        if (pathCursor != null) {
            if(pathCursor.moveToNext()){
               paths = pathCursor.getString(pathCursor.getColumnIndexOrThrow(MapClomunsContants.PATH_POINTS));
            }
            pathCursor.close();
        }

        if (!TextUtils.isEmpty(paths)) {
            Log.e(TAG, "get path from cp ->"+ paths);
            arrPaths=paths.split(POINT_SEPERATOR);
        }

        return  arrPaths;
    }


}
