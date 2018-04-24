package com.ubtechinc.cruiser.wayguide.interactor.impl;

import com.ubtechinc.cruiser.wayguide.model.IWayGuiderManager;
import com.ubtechinc.cruiser.wayguide.model.WayGuiderManager;
import com.ubtechinc.cruiser.wayguide.interactor.IWayGuiderInteractor;

/**
 * Created on 2017/2/24.
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 游览功能模型类
 */
public class WayGuiderInteractorImpl implements IWayGuiderInteractor {
    private IWayGuiderManager mWGManager;

    public WayGuiderInteractorImpl() {
        mWGManager= WayGuiderManager.getInstance(null);
    }

    @Override
    public void start() {
        mWGManager.start();
    }

    @Override
    public void reset() {
        mWGManager.reset();
    }

    @Override
    public void pause() {
        mWGManager.pause();
    }

    @Override
    public void end() {
        mWGManager.end();
    }

    @Override
    public void next() {
        mWGManager.next();
    }

    @Override
    public void introAgain() {
        mWGManager.introAgain();
    }

    @Override
    public void returnParkPoint() {
        mWGManager.returnParkPoint();
    }
}
