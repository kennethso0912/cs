package com.ubtechinc.cruiser.wayguide.interactor.impl;

import android.content.Context;
import android.util.Log;

import com.ubtechinc.cruiser.wayguide.app.AppApplication;
import com.ubtechinc.cruiser.wayguide.interactor.IUserValidateInteractor;
import com.ubtechinc.cruzr.userverify.api.CruzrSecurityApi;
import com.ubtechinc.cruzr.userverify.api.ISecurityCallBack;

/**
 * Created on 2017/3/4.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des  用户权限校验模型
 */

public class UserValidateInteractorImpl implements IUserValidateInteractor {
    private static final String TAG = "UserValidateInteractorI";
    private Context mContext;
    private CruzrSecurityApi mUserManagerApi;
    private static final int ADMIN_VALUE = 3;//调用管理员权限api接口权限值 2:游客 3：管理员 4：超级管理员

    public UserValidateInteractorImpl(){
          this.mContext= AppApplication.getContext();
          onCreate();
    }


    @Override
    public void onCreate() {
           init();
    }

    @Override
    public boolean validate() {
        int rst= mUserManagerApi.getAdminiMode();
        Log.e(TAG, "validate: mode->"+rst);
        return rst>=ADMIN_VALUE;
//        return true;
    }

    private void init(){
        mUserManagerApi =new CruzrSecurityApi(this.mContext,mSCB);
    }

    private ISecurityCallBack mSCB =new ISecurityCallBack() {
        @Override
        public void securityModeChange(int i) {
            Log.e(TAG, "securityModeChange: 管理权限值 i->"+i);
        }

        @Override
        public void onBindSuccess() {
            Log.e(TAG, "onBindSuccess: 管理员权限检测绑定成功");
        }

        @Override
        public void onBindfail() {
            Log.e(TAG, "onBindfail: 管理员权限检测绑定失败");
        }

        @Override
        public void onDisconnect() {
            Log.e(TAG, "onDisconnect: 管理员权限检测被解绑");
        }
    };
}
