package com.ubtechinc.cruiser.wayguide.model;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created on 2017/2/27.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 任务点空闲检测器
 */

public class TaskIdleTimeoutChecker extends Thread {
    private static final String TAG = "TaskIdleTimeoutChecker";
    private static final long  TIME_OUT= 5*1000;
    private static final int THREAD_POOL_NUM = 1;
    private boolean mIsStopRun;
    private long mLastTime=System.currentTimeMillis();
    private IdleTimeoutCallback mCallback;


    public TaskIdleTimeoutChecker(IdleTimeoutCallback mCallback){
        this.mCallback=mCallback;
    }

    private ExecutorService mExecutor= Executors.newFixedThreadPool(THREAD_POOL_NUM);
    private ExecutorService mExecutor1= Executors.newCachedThreadPool();

    private void update(){
        this.mLastTime=System.currentTimeMillis();
    }

    /**
     * 开始idle计时器
     */
    public void start(){
        mIsStopRun=false;
        update();
        mExecutor.execute(this);
    }


    @Override
    public void run() {
        while (!mIsStopRun){
            synchronized (this){
                if(System.currentTimeMillis()-mLastTime>=TIME_OUT){
                    if(mCallback!=null){

                        mCallback.timeout();
                        mCallback=null;

                        mIsStopRun=true;
                        return;
                    }
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isTimeout(){
        return System.currentTimeMillis()-mLastTime>=TIME_OUT;
    }

    /**
     * 关闭idle超时器
     */
    public void close(){
        mIsStopRun=true;
        this.interrupt();

    }

    /**
     * idle超时回调接口
     */
    public interface  IdleTimeoutCallback{
        void timeout();
    }


}
