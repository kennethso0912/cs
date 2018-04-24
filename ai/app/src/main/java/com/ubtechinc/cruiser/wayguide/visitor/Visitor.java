package com.ubtechinc.cruiser.wayguide.visitor;

/**
 * Created on 2017/11/2.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public interface Visitor {
    void success(StateSuccess state);
    void fail(StateFail state);
}