package com.ubtechinc.cruiser.wayguide.presenter;

import com.ubtechinc.cruiser.wayguide.callback.IDataLoadCallback;

/**
 * Created on 2017/3/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des
 */

public interface IDataHandlerPresenter {
    /**
     * 游览数据的加载
     */
    void load();
    void load(IDataLoadCallback cb);

}