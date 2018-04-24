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
import com.ubtechinc.cruiser.wayguide.model.Media;
import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;

import java.util.ArrayList;

import static com.ubtechinc.cruiser.wayguide.map.MapClomunsContants.IMAGE_PLAY_TIME;
import static com.ubtechinc.cruiser.wayguide.map.MapClomunsContants.IMAGE_URL;
import static com.ubtechinc.cruiser.wayguide.map.MapClomunsContants.IS_USE;
import static com.ubtechinc.cruiser.wayguide.map.MapClomunsContants.MAP_ID;
import static com.ubtechinc.cruiser.wayguide.map.MapClomunsContants.MAP_NAME;
import static com.ubtechinc.cruiser.wayguide.map.MapClomunsContants.MAP_POINT_ID;
import static com.ubtechinc.cruiser.wayguide.map.MapClomunsContants.MAP_X;
import static com.ubtechinc.cruiser.wayguide.map.MapClomunsContants.MAP_Y;
import static com.ubtechinc.cruiser.wayguide.map.MapClomunsContants.MEDIA_TYPE;
import static com.ubtechinc.cruiser.wayguide.map.MapClomunsContants.PATH_ID;
import static com.ubtechinc.cruiser.wayguide.map.MapClomunsContants.POINT_NAME;
import static com.ubtechinc.cruiser.wayguide.map.MapClomunsContants.RETURN_POINT;
import static com.ubtechinc.cruiser.wayguide.map.MapClomunsContants.THETA;
import static com.ubtechinc.cruiser.wayguide.map.MapClomunsContants.TTS;
import static com.ubtechinc.cruiser.wayguide.map.MapClomunsContants.URI_CUR_PATH_ALL_POINT;
import static com.ubtechinc.cruiser.wayguide.map.MapClomunsContants.URI_POINT_MEDIAS;
import static com.ubtechinc.cruiser.wayguide.map.MapClomunsContants.VIDEO_URL;
import static com.ubtechinc.cruiser.wayguide.utils.ParserUtil.extractMapNameFromPath;
import static com.ubtechinc.cruiser.wayguide.utils.ParserUtil.getString;
import static com.ubtechinc.cruiser.wayguide.utils.ParserUtil.stringToFloat;


/**
 * Created on 2017/3/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 对应地图的所有路线的媒体类型（预下载文件数据）
 */

public class MapNewDBProvider  implements IMapProvider{
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

    private String[] wayGuideClomuns={MapClomunsContants.NAME,MapClomunsContants.X,MapClomunsContants.Y,MapClomunsContants.CONTENT,MapClomunsContants.THETA};
    private String[] pathClomuns={MapClomunsContants.PATH_POINTS};
    private String[] pointsInPath;

    public MapNewDBProvider(Context context){
         this.mContext=context;
         this.mResolver = this.mContext.getContentResolver();

         //监听游览数据变化
         this.mResolver.registerContentObserver(WAYGUIDER_PATH_URI,true,mapContentObserver);
         this.mResolver.registerContentObserver(ALL_POINT_URI,true,mapContentObserver);
    }


    public MapInfo get() {
        return null;
    }


    public MapNewInfo get(IDataLoadCallback cb) {
        long startTime=System.currentTimeMillis();

        MapNewInfo mapInfo=null;

        curMapPath = RosRobotApi.get().getCurrentMap();
        Log.e(TAG, "curMapPath->"+curMapPath);

        if (TextUtils.isEmpty(curMapPath)) {
              Log.e(TAG, "get: ->fail to get path of map");
              if(cb!=null) {
                  cb.onFail(getString(R.string.no_map));
              }
            return null;
        }
        mapInfo=new MapNewInfo();

        //extract the map name from path of map
        String curMapName = extractMapNameFromPath(curMapPath);
        Log.e(TAG, "get: curMap->"+curMapName);

        //fetch all wayguide point from map cp
        ArrayList<MapNewInfo.Location> commonPoints=getPointsByMap(mResolver,curMapName);

        if(commonPoints.isEmpty()){
            if(cb!=null) {
                cb.onFail(getString(R.string.no_wayguide_path));
            }
            return null;
        }

        for (MapNewInfo.Location point:commonPoints) {
            int map_point_id=point.getMap_point_id();
            int map_id=point.getMap_id();
            int path_id=point.getPath_id();
            ArrayList<Media> medias=getMediasByCommonPoint(mResolver,map_id,path_id,map_point_id);

            point.setMedias(medias);
        }

        Log.e(TAG, "当前地图当前路线数据 commonPoints"+commonPoints);
        mapInfo.location=commonPoints;

        //有设置返回位置，则读取返回点的坐标
        if(!TextUtils.isEmpty(return_point)){
              MapNewInfo.Location loc=getReturnPoint(mResolver,return_point,curMapName);
              mapInfo.returnPoint=loc;
        }

        long endTime=System.currentTimeMillis();
        Log.e(TAG, "get mapInfo: costTime->"+(endTime-startTime));

        return mapInfo;
    }


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
     * 获取游览返回点
     * @param resolver
     */
    private MapNewInfo.Location getReturnPoint(ContentResolver resolver, String pointName, String curMap) {
        MapNewInfo.Location loc=null;
        String[] args={pointName,curMap};

        Cursor cursor=resolver.query(ALL_POINT_URI,wayGuideClomuns, MapClomunsContants.POINT_NAME+" = ? and "+MapClomunsContants.Map_NAME+"= ?",args,null);
        if (cursor != null&&cursor.moveToNext()) {

            loc=new MapNewInfo.Location();

            float x=stringToFloat(cursor.getString(cursor.getColumnIndexOrThrow(MapClomunsContants.X)));
            float y=stringToFloat(cursor.getString(cursor.getColumnIndexOrThrow(MapClomunsContants.Y)));
            float theta=stringToFloat(cursor.getString(cursor.getColumnIndexOrThrow(THETA)));
            String name=cursor.getString(cursor.getColumnIndexOrThrow(MapClomunsContants.NAME));

            loc.setName(name);
            loc.setX(x);
            loc.setY(y);
            loc.setTheta(theta);

            Log.e(TAG, "getUnSortWGPoints: name="+name+";x="+x+";y="+y+";theta="+theta);

            cursor.close();
        }
        return loc;
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

        Cursor pathCursor = resolver.query(WAYGUIDER_PATH_URI,pathClomuns,MAP_NAME+"= ?",args,null);
        if (pathCursor != null&&pathCursor.moveToNext()) {

            paths = pathCursor.getString(pathCursor.getColumnIndexOrThrow(MapClomunsContants.PATH_POINTS));
            pathCursor.close();
        }

        if (!TextUtils.isEmpty(paths)) {
            Log.e(TAG, "get path from cp ->"+ paths);
            arrPaths=paths.split(POINT_SEPERATOR);
        }

        return  arrPaths;
    }

    private String return_point;
    /**
     * 获取当前地图当前路线的所有普通点
     * @param resolver
     */
    private ArrayList<MapNewInfo.Location> getPointsByMap(ContentResolver resolver,String curMap) {
        ArrayList<MapNewInfo.Location> locs = new ArrayList<>();
        MapNewInfo.Location loc;

        Uri ALL_POINT_URI = Uri.parse(URI_CUR_PATH_ALL_POINT);
        String[] args={curMap,"true"};
        String[] wayGuideClomuns={POINT_NAME,MAP_X,MAP_Y,MAP_NAME,MAP_ID,PATH_ID,MAP_POINT_ID,THETA,RETURN_POINT};

        Cursor cursor=resolver.query(ALL_POINT_URI,wayGuideClomuns, MAP_NAME+" = ? and "+IS_USE+" = ?",args,null);

        return_point="";
        if (cursor != null&&cursor.moveToFirst()) {
            while (!cursor.isAfterLast()){
                int map_id=cursor.getInt(cursor.getColumnIndexOrThrow(MAP_ID));
                int path_id=cursor.getInt(cursor.getColumnIndexOrThrow(PATH_ID));
                int map_point_id=cursor.getInt(cursor.getColumnIndexOrThrow(MAP_POINT_ID));

                float map_x=stringToFloat(cursor.getString(cursor.getColumnIndexOrThrow(MAP_X)));
                float map_y=stringToFloat(cursor.getString(cursor.getColumnIndexOrThrow(MAP_Y)));
                float theta=stringToFloat(cursor.getString(cursor.getColumnIndexOrThrow(THETA)));

                String map_name=cursor.getString(cursor.getColumnIndexOrThrow(MAP_NAME));
                String point_name=cursor.getString(cursor.getColumnIndexOrThrow(POINT_NAME));

                return_point=!TextUtils.isEmpty(return_point)?return_point:cursor.getString(cursor.getColumnIndex(RETURN_POINT));

                Log.e(TAG, "getPointsByMap-> point_name="+point_name+",map_x="+map_x+",map_y="+map_y+",map_point_id="+map_point_id+",theta="+theta+",map_name="+map_name+",map_id="+map_id+",path_id="+path_id+",return_point="+return_point);

                loc=new MapNewInfo.Location();
                loc.setName(point_name);
                loc.setX(map_x);
                loc.setY(map_y);
                loc.setTheta(theta);
                loc.setMap_point_id(map_point_id);
                loc.setMap_id(map_id);
                loc.setPath_id(path_id);

                locs.add(loc);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return locs;
    }


    /**
     * 根据当前普通点的map_point_id查找所有媒体点数据
     * @param resolver
     * @param map_id
     * @param path_id
     * @param map_point_id
     * @return
     */
    private  ArrayList<Media> getMediasByCommonPoint(ContentResolver resolver,int map_id,int path_id,int map_point_id){
        ArrayList<Media> medias=new ArrayList<>();
        Media media;

        Uri ALL_POINT_URI = Uri.parse(URI_POINT_MEDIAS);
        String[] args={map_id+"",path_id+"",map_point_id+""};
        String[] wayGuideClomuns={MEDIA_TYPE,TTS,IMAGE_URL,IMAGE_PLAY_TIME,VIDEO_URL,MAP_POINT_ID};

        Cursor cursor=resolver.query(ALL_POINT_URI,wayGuideClomuns, "routinepointmediamodel.map_id = ?  and routinepointmediamodel.path_id = ? and "+MAP_POINT_ID+" = ?",args,null);
        if (cursor != null&&cursor.moveToFirst()) {
            while (!cursor.isAfterLast()){
                int media_type=cursor.getInt(cursor.getColumnIndexOrThrow(MEDIA_TYPE));
                int map_point_id1=cursor.getInt(cursor.getColumnIndexOrThrow(MAP_POINT_ID));
                long image_play_time=cursor.getLong(cursor.getColumnIndexOrThrow(IMAGE_PLAY_TIME));

                String tts=cursor.getString(cursor.getColumnIndexOrThrow(TTS));
                String image_url=cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_URL));
                String video_url=cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_URL));

                Log.e(TAG, "getMediasByMapPointId-> map_point_id1="+map_point_id1+",media_type="+media_type+",tts="+tts+",image_url="+image_url+",video_url="+video_url+",image_play_time="+image_play_time);

                media=new Media();
                media.setType(media_type);
                media.setTts(tts);
                media.setImgUrl(image_url);
                media.setVideoUrl(video_url);
                media.setImgPlayTime(image_play_time);

                medias.add(media);

                cursor.moveToNext();
            }
            cursor.close();
        }

        return medias;
    }


}
