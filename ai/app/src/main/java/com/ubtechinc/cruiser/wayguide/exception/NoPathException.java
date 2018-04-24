package com.ubtechinc.cruiser.wayguide.exception;

/**
 * Created on 2017/4/5.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 没有游览路线异常
 */
public class NoPathException extends Exception {
    public NoPathException(String detailMsg){
        super(detailMsg);
    }
}