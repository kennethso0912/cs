package com.ubtechinc.cruiser.wayguide.presenter.base;


/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des
 */

public abstract class AbstractPresenter {
    public  void onCreate(){
        registerEvent();
    }

    public  void onDestroy(){
        unRegisterEvent();
    }

    protected  void registerEvent(){}

    protected  void unRegisterEvent(){}
}