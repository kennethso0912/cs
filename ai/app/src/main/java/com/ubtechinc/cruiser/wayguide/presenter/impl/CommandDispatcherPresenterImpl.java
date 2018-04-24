package com.ubtechinc.cruiser.wayguide.presenter.impl;

import android.content.Context;
import com.ubtechinc.cruiser.wayguide.interactor.ICommandDispatcherInteractor;
import com.ubtechinc.cruiser.wayguide.interactor.impl.CommandNewDispatcherInteractorImpl;
import com.ubtechinc.cruiser.wayguide.presenter.ICommandDispatcherPresenter;
import com.ubtechinc.cruiser.wayguide.presenter.base.AbstractPresenter;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc  指令分发业务类(因为触发是SDK，所以这里没有消息通讯的注册)
 */

public final class CommandDispatcherPresenterImpl extends AbstractPresenter implements ICommandDispatcherPresenter {
    private ICommandDispatcherInteractor mInteractor;

    @Override
    public void onCreate() {
        regDispatcher();
    }

    public CommandDispatcherPresenterImpl(Context context){
        mInteractor = new CommandNewDispatcherInteractorImpl(context);
    }

    @Override
    public void regDispatcher() {
        mInteractor.regDispatcher();
    }
}
