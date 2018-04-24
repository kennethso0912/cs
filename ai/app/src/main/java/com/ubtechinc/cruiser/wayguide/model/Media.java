package com.ubtechinc.cruiser.wayguide.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.ubtechinc.cruiser.wayguide.app.AppApplication;
import com.ubtechinc.cruiser.wayguide.download.DownloadUtil;
import com.ubtechinc.cruiser.wayguide.utils.UbtConstant;

/**
 * Created on 2017/7/5.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class Media implements Parcelable{
    //媒体类型 0:纯TTS  1:纯图片 2：纯视频 3：TTS+图片
    private int type;
    private String tts;
    private String imgUrl;
    private String videoUrl;
    private long imgPlayTime;

    public static final int  TYPE_TASK_TTS= 0;
    public static final int  TYPE_TASK_IMAGE= 1;
    public static final int  TYPE_TASK_VIDEO= 2;
    public static final int  TYPE_TASK_TTS_IMAGE= 3;

    public Media(){}

    public Media(int type,long imgPlayTime,String tts,String imgUrl,String videoUrl){
        this.type=type;
        this.imgPlayTime=imgPlayTime;
        this.imgUrl=imgUrl;
        this.videoUrl=videoUrl;
        this.tts=tts;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTts() {
        return tts;
    }

    public void setTts(String tts) {
        this.tts = tts;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public long getImgPlayTime() {
        return imgPlayTime;
    }

    public void setImgPlayTime(long imgPlayTime) {
        this.imgPlayTime = imgPlayTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
         dest.writeInt(type);
         dest.writeLong(imgPlayTime);
         dest.writeString(tts);
         dest.writeString(imgUrl);
         dest.writeString(videoUrl);
    }

    public static final Parcelable.Creator<Media> CREATOR = new Creator<Media>() {

        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source.readInt(), source.readLong(),source.readString(),source.readString(),source.readString());
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };


    @Override
    public String toString() {
        return "[type="+type+";tts="+tts+";imgUrl="+imgUrl+";imgPlayTime="+imgPlayTime+";videoUrl="+videoUrl+"]";
    }

    public String getMediaAction() {
        String action="";
        String url="";

        if(type==TYPE_TASK_TTS){
            action= UbtConstant.INTENT_TTS;
        }else{
            action=UbtConstant.INTENT_PIC_VIDEO;
            if(type==TYPE_TASK_VIDEO){
                url=this.getVideoUrl();
            }else{
                url=this.getImgUrl();
            }
        }

        if(!TextUtils.isEmpty(url)) {
            String imageFilePath = DownloadUtil.getLocalFilePathByUrl(AppApplication.getContext(), url);
            if (TextUtils.isEmpty(imageFilePath)) {
                action = UbtConstant.INTENT_TTS;
            }
        }
        return action;
    }

}
