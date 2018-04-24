package com.ubtechinc.cruiser.wayguide.interactor;

import com.ubtechinc.cruiser.wayguide.exception.NoMapException;
import com.ubtechinc.cruiser.wayguide.exception.NoPathException;
import com.ubtechinc.cruiser.wayguide.callback.IDataLoadCallback;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des
 */

public interface IDataHandlerInteractor {
    /**
     * 游览数据的加载
     */
     void load();
     void load(IDataLoadCallback cb);
     void checkData() throws NoMapException, NoPathException;
}
