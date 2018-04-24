/*
 *
 *  *
 *  *  *
 *  *  * Copyright (c) 2008-2017 UBT Corporation.  All rights reserved.  Redistribution,
 *  *  *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 *  *  *
 *  *
 *
 */

package com.ubtrobot.ops;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ubtrobot.ledcmds.jni.LedControl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;

import timber.log.Timber;

/**
 * @desc : 机器人灯控操作控制
 * @author: Logic
 * @email : logic.peng@ubtech.com
 * @time : 2017/4/17
 * @modifier:
 * @modify_time:
 */

public final class LedOpController {
    private static final String TAG = LedOpController.class.getSimpleName();
    private ExecutorService eyeOpThread;//眼睛操作线程
    private ExecutorService headOpThread;//头灯操作线程
    private ExecutorService mouthOpThread;//嘴巴灯
    private ExecutorService otherOpThread;//其它灯操作线程
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private LedOpController(){
        eyeOpThread = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, "eyeOpThread");
            }
        });
        headOpThread = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r,"headOpThread");
            }
        });
        mouthOpThread = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r,"mouthOpThread");
            }
        });
        otherOpThread = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, "otherOpThread");
            }
        });
        //接管灯效
        LedControl.open();
        LedControl.ledSetOFF(0);
        LedControl.close();
    }

    public static LedOpController instance(){
        return Holder.mOpsManager;
    }
    private static class Holder {
        private static LedOpController mOpsManager = new LedOpController();
    }

    public synchronized void destroy(){
        eyeOpThread.shutdownNow();
        headOpThread.shutdownNow();
        mouthOpThread.shutdownNow();
        otherOpThread.shutdownNow();
    }

    //一个串口操作
    public void executeOp(final ICmdOp op, final LedOpResultListener listener){
        if (op.getOpType() == ICmdOp.TYPE_HEAD){
            headOpThread.execute(new Runnable() {
                @Override
                public void run() {
                    executeOpInner(op, listener);
                }
            });
            return;
        }

        if (op.getOpType() == ICmdOp.TYPE_EYE) {
            eyeOpThread.execute(new Runnable() {
                @Override
                public void run() {
                    executeOpInner(op, listener);
                }
            });
            return;
        }

        if (op.getOpType() == ICmdOp.TYPE_MOUTH){
            mouthOpThread.execute(new Runnable() {
                @Override
                public void run() {
                    executeOpInner(op, listener);
                }
            });
        }

        if (op.getOpType() != ICmdOp.TYPE_EYE && op.getOpType() != ICmdOp.TYPE_HEAD){
            otherOpThread.execute(new Runnable() {
                @Override
                public void run() {
                    executeOpInner(op, listener);
                }
            });
            return;
        }
    }

    private void executeOpInner(ICmdOp op, final LedOpResultListener listener) {
        op.prepare();
        final LedOpResult re = op.start();
        if (listener != null)
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onRecvOpResult(re);
                }
            });
    }

    public void executeOp(final ICmdOp op){
        executeOp(op, new LedOpResultListener() {
            @Override
            public void onRecvOpResult(LedOpResult result) {
                if (result != null) {
                    Timber.d("opTpe=%d, success= %s", op.getOpType(), result.success);
                }else {
                    Timber.w("op=%d interrupted..",op.getOpType());
                }
            }
        });
    }

    // FIXME: 2017/8/24 待完善
    @Nullable
    public LedOpResult executeOpSync(final ICmdOp op){
        Callable<LedOpResult> callable = new Callable() {
            @Override
            public LedOpResult call() throws Exception {
                op.prepare();
                LedOpResult result = op.start();
                return result;
            }
        };
        FutureTask<LedOpResult> task = new FutureTask<LedOpResult>(callable);
        executeOpInner(op.getOpType(), task);
        try {
            return task.get();
        } catch (InterruptedException e) {
            Timber.w(e.getMessage());
        } catch (ExecutionException e) {
            Timber.w(e.getMessage());
        }
        return null;
    }

    private void executeOpInner(int opType, FutureTask<LedOpResult> task) {
        if (opType != ICmdOp.TYPE_EYE && opType != ICmdOp.TYPE_HEAD){
            otherOpThread.execute(task);
        }else if (opType == ICmdOp.TYPE_HEAD){
            headOpThread.execute(task);
        }else {
            eyeOpThread.execute(task);
        }
    }
}
