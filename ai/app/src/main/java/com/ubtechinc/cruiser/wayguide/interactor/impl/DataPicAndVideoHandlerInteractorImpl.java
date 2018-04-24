package com.ubtechinc.cruiser.wayguide.interactor.impl;

import android.util.Log;

import com.ubtechinc.cruiser.wayguide.exception.NoMapException;
import com.ubtechinc.cruiser.wayguide.exception.NoPathException;
import com.ubtechinc.cruiser.wayguide.callback.IDataLoadCallback;
import com.ubtechinc.cruiser.wayguide.interactor.IDataHandlerInteractor;
import com.ubtechinc.cruiser.wayguide.map.IMapProvider;
import com.ubtechinc.cruiser.wayguide.map.MapInfo;
import com.ubtechinc.cruiser.wayguide.map.MapProvider;
import com.ubtechinc.cruiser.wayguide.model.WayGuiderManager;
import com.ubtechinc.cruiser.wayguide.task.IWayGuiderTask;
import com.ubtechinc.cruiser.wayguide.task.impl.WayGuiderTaskImpl;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created on 2017/3/4.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 游览数据加载模型类
 */

public class DataPicAndVideoHandlerInteractorImpl implements IDataHandlerInteractor ,Runnable{
    private static final String TAG = "DataHandlerInteractorIm";
    private static final int THREAL_MAX_NUM = 1;
//    private IMapProvider mapProvider = new MapDBProvider(AppApplication.getContext());
    private IMapProvider mapProvider = new MapProvider();
    private MapInfo mapInfo;
    private ArrayList<MapInfo.Location> locs;
    private ExecutorService mExecutor= Executors.newFixedThreadPool(THREAL_MAX_NUM);
    private IDataLoadCallback cb;

    @Override
    public void load() {
         mExecutor.execute(this);
    }

    @Override
    public void load(IDataLoadCallback cb) {
         this.cb=cb;
         load();
    }

    @Override
    public void checkData() throws NoMapException, NoPathException {
        mapProvider.checkData();
    }

    @Override
    public void run() {
        if(this.cb==null){
            Log.e(TAG, "run: 游览数据加载回调对象为空->");
            return;
        }

        mapInfo=(MapInfo) mapProvider.get(this.cb);

        if (mapInfo == null) {
            Log.e(TAG, "run:  地图数据mapInfo为空->");
            return;
        }

        locs=mapInfo.location;

        //构造游览任务置入队列中
        ArrayList<IWayGuiderTask> tasks=new ArrayList<IWayGuiderTask>();
        for (int i = 0; i < locs.size(); i++) {
            tasks.add(new WayGuiderTaskImpl(locs.get(i)));
        }

        //初始化游览任务列表
        WayGuiderManager.getInstance(tasks);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.cb.onFinish();
    }

}
