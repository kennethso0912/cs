package com.ubtechinc.cruiser.wayguide.map;

/**
 * Created on 2017/3/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 地图map数据库表字段集合类
 */

public class MapClomunsContants {
    //pointModel表的字段
    public static final String Map_NAME = "mapName";
    public static final String NAME ="pointName";
    public static final String X ="map_x";
    public static final String Y ="map_y";
    public static final String CONTENT = "description";
    public static final String POINT_TYPE="pointType";

    //游览任务类型字段 TTS  图片  视频  TTS+图片
    public static final String WAY_GUIDER_TYPE="";

    //travelModel表的字段
    public static final String PATH_POINTS="path_points";

    //travelpointmodel以上两个表的关联关系表
//    public static final String PATH_ID="path_id";
    public static final String POINT_ID="point_index";

    public static final String URI_CUR_PATH_ALL_POINT="content://com.ubtechinc.cruzr.map.routinepathProvider/routinepath";
    public static final String POINT_NAME= "pointName";
    public static final String MAP_X = "map_x";
    public static final String MAP_Y = "map_y";
    public static final String MAP_NAME = "map_name";
    public static final String MAP_ID = "map_id";
    public static final String PATH_ID = "routine_id";
    public static final String MAP_POINT_ID = "map_point_id";
    public static final String THETA = "theta";
    public static final String RETURN_POINT = "return_point";
    public static final String IS_USE = "routinemodel.is_use";

    public static final String URI_POINT_MEDIAS = "content://com.ubtechinc.cruzr.map.routinepathProvider/mappointmedia";
    public static final String MEDIA_TYPE = "media_type";
    public static final String TTS = "tts";
    public static final String IMAGE_URL = "image_url";
    public static final String IMAGE_PLAY_TIME = "image_play_time";
    public static final String VIDEO_URL = "video_url";



    private MapClomunsContants(){}

}
