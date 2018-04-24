package com.ubtrobot.led.listener;

import android.support.annotation.Nullable;

/**
 * @desc :
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/9/14
 * @modifier:
 * @modify_time:
 */

public interface ResponseListener<T> {
    /**
     * @description 调用成功
     * @param
     * @return
     * @throws
     */
    void onResponseSuccess(T data);
    /**
     * @description 调用失败
     * @param errCode 0表示成功，其它表示错误
     * @param errMsg  错误描述
     * @return
     * @throws
     */
     void onFailure(int errCode, @Nullable String errMsg);
}
