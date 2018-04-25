package com.ubtechinc.cruiser.wayguide.visitor;

/**
 * Created on 2017/11/2.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class StateSuccess implements State{
    private SuccessCallback cb;

    @Override
    public void accept(Visitor visitor) {
        visitor.success(this);
    }

    public void regCallback(SuccessCallback cb){
        this.cb=cb;
    }

    public SuccessCallback getCallback(){
        return this.cb;

    }

    interface SuccessCallback{
        void onSuccess();
    }
}
