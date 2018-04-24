package com.ubtechinc.cruiser.wayguide.visitor;
/**
 * Created on 2017/11/2.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */public class PauseVisitor implements Visitor{
    @Override
    public void success(StateSuccess state) {
        //暂停成功后操作
        state.getCallback().onSuccess();
    }

    @Override
    public void fail(StateFail state) {
        //暂停失败后操作
        state.getCallback().onFail();
    }
}