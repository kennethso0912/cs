package com.ubtechinc.cruiser.wayguide.visitor;

/**
 * Created on 2017/11/2.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class StateFail implements State{
    private FailCallback cb;

    @Override
    public void accept(Visitor visitor) {
        visitor.fail(this);
    }

    public void regCallback(FailCallback cb){
        this.cb=cb;
    }

    public FailCallback getCallback(){
        return this.cb;
    }

    interface FailCallback{
        void onFail();
    }
}
