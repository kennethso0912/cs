package com.ubtrobot.service;

import android.content.Context;

import com.ubtrobot.led.ILedContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created on 2017/10/9.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc LED具体产品注入器
 */
@SuppressWarnings("unchecked")
public class LedInjector {
    private Context mContext;
    public LedInjector(Context context){
        this.mContext=context;

    }

    public ILedContext provideSpeechApi() {
        try {
            Class<ILedContext> clazz = (Class<ILedContext>) Class.forName(BuildConfig.ILedContext);
            Constructor<ILedContext> con = clazz.getConstructor();
            return con.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
