package com.ubtechinc.cruiser.wayguide.model;

import android.util.Log;

import com.ubtechinc.cruiser.wayguide.R;
import com.ubtechinc.cruiser.wayguide.app.AppApplication;
import com.ubtechinc.cruiser.wayguide.callback.ITaskCallback;
import com.ubtechinc.cruiser.wayguide.callback.ITtsTaskCallback;
import com.ubtechinc.cruiser.wayguide.event.Event;
import com.ubtechinc.cruiser.wayguide.map.MapNewInfo;
import com.ubtechinc.cruiser.wayguide.task.IGoTask;
import com.ubtechinc.cruiser.wayguide.task.ISayTask;
import com.ubtechinc.cruiser.wayguide.task.IWayGuiderTask;
import com.ubtechinc.cruiser.wayguide.task.impl.GoTaskImpl;
import com.ubtechinc.cruiser.wayguide.task.impl.SayTaskImpl;
import com.ubtechinc.framework.log.NLog;
import com.ubtechinc.framework.notification.NotificationCenter;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created on 2017/2/24.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des  任务控制管理器
 */

public class WayGuiderManager implements Runnable , IWayGuiderManager{
    private final static String TAG="WayGuiderManager";
    private static final TaskDeque<IWayGuiderTask> mQueue = new TaskDeque<>();
    private volatile static IWayGuiderTask mCurrentTask;
    private ExecutorService mExecutor= Executors.newSingleThreadExecutor();
    private volatile boolean isLoop;
    private final ReentrantLock mLock=new ReentrantLock(true);

    //机器人讲解完的停驻点
    private MapNewInfo.Location mReturnParkPoint;

    public void setReturnParkPoint(MapNewInfo.Location mReturnParkPoint) {
        this.mReturnParkPoint = mReturnParkPoint;
    }

    private WayGuiderManager(){}

    private static class WayGuiderManagerInstance{
        static WayGuiderManager INSTANCE=new WayGuiderManager();
    }


    public static WayGuiderManager getInstance(ArrayList<IWayGuiderTask> tasks) {
        if (tasks == null) {
            return WayGuiderManagerInstance.INSTANCE;
        }
        synchronized (mQueue) {
            if (!hasAnyTask()) {
                Log.e(TAG, "getInstance:可以添加游览任务->");
                enqueue(tasks);
            }
        }
        return WayGuiderManagerInstance.INSTANCE;
    }

    @Override
    public void run() {
        long curTime=System.currentTimeMillis();
        while(isLoop) {
            try {
                Thread.sleep(1000);//队列遍历时隔
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            IWayGuiderTask task;
            synchronized (mQueue) {
                task = mQueue.peek();

                //判断队列是否为空
                if (task == null) {
                    synchronized (mQueue) {
                        task = mQueue.peek();
                        if (task == null) {
                            isLoop = false;
                            Log.e(TAG, "run: 游览队列为空，正常结束");
                            return;
                        }
                    }
                }
            }

            synchronized (mLock) {
                if (!isLoop) {
                      Log.e(TAG, "run: middle !isLoop->中间检测到被停止");
                    return;
                }
            }

            boolean isf=false;

            //判断当前游览任务的执行情况
            if (mCurrentTask == null || mCurrentTask != null && (isf = mCurrentTask.isFinish())) {
                    synchronized (mLock) {
                        if (!isLoop) {
                            Log.e(TAG, "run: next->!isLoop->已进入当前游览任务为空或结束的方法检测到被停止");
                            return;
                        }
                    }

                    NLog.e(TAG, "loop->" + task.getName() + "  游览点开始in  start time->" + System.currentTimeMillis());

                    synchronized (mQueue) {
                        mCurrentTask = task;
                        mQueue.remove(task);
                        Log.e(TAG, "run: 移除后mCurrentTask："+mCurrentTask.toString());
                    }
                    Log.e(TAG, "run: 移除后同步块外的mCurrentTask："+mCurrentTask.toString());
                    task.start();
            }

            if(System.currentTimeMillis()-curTime>=120000) {
                curTime=System.currentTimeMillis();
                Log.e("twgstoploop", "loop per run: end->"+(mCurrentTask!=null?mCurrentTask.getName():"")+"  "+isf+"->"+mCurrentTask.toString());
            }
        }
    }


    private static void enqueue(ArrayList<IWayGuiderTask> tasks)
    {
        mQueue.enqueue(tasks);
    }

    /**
     * 开始执行游览任务队列
     */
    public void start(){
        Log.e(TAG, "start: mQueue.size()="+mQueue.size());
        mCurrentTask=null;

        setIsStop(false);

        //防止异常情况下未恢复停泊状态
        setIsParking(false);

        shoot();
    }

    /**
     * 暂停游览任务执行
     */
    public void pause()
    {
        synchronized (mLock) {
            Log.e(TAG, "pause: this.isLoop = false->");
            this.isLoop = false;
        }

        Log.e(TAG, "pause: mCurrentTask != null && !mCurrentTask.isFinish():"+(mCurrentTask != null && !mCurrentTask.isFinish()));
        if (mCurrentTask != null && !mCurrentTask.isFinish()) {
            NLog.e(TAG, "in pause->" + mCurrentTask.getName() + "->mCurrentTask:"+mCurrentTask.toString() );
            synchronized (mQueue) {
                if (!mQueue.contains(mCurrentTask)) {
                    Log.e(TAG, "pause: 暂停插入mCurrentTask:"+mCurrentTask.toString());
                    mQueue.addFirst(mCurrentTask);
                }
            }

        }
        mCurrentTask.stop();

        setIsStop(true);
    }

    /** 以前是每个任务切片同步，不够线程安全，改为全局标识 打断true/false**/
    private static boolean isStop=false;

    public static boolean isIsStop() {
        synchronized (WayGuiderManager.class) {
            return isStop;
        }
    }

    private static void setIsStop(boolean isStop) {
        synchronized (WayGuiderManager.class) {
            WayGuiderManager.isStop = isStop;
        }
    }
    /** 以前是每个任务切片同步，不够线程安全，改为全局标识 打断true/false**/

    @Override
    public void end() {
        cancel();
    }

    /**
     * 重新开始被暂停的任务
     */
    public void reset(){
        Log.e(TAG, "reset: mQueue.size()="+mQueue.size());
        mCurrentTask=null;

        setIsStop(false);
        shoot();
    }

    private void shoot(){
        Log.e(TAG, "shoot: isLoop->"+isLoop);
        if(!isLoop) {
            isLoop=true;
            mExecutor.execute(this);
        }
    }

    /**
     * 取消队列任务执行，并清除还未执行的任务
     */
    private  void cancel(){
         synchronized (mLock) {
            isLoop = false;
         }

         synchronized (mQueue) {
             clearQueue();
         }

         if (mCurrentTask != null && !mCurrentTask.isFinish()) {
                mCurrentTask.stop();

                setIsStop(true);
//                setIsStop(false);
         }
    }

    /**
     * 清空队列
     */
    private void clearQueue(){
        mQueue.clear();
    }

    /**
     * 是否存在任务
     * @return true 有任务 false 无任务
     */
    private static boolean hasAnyTask(){
        return !mQueue.isEmpty();
    }

    public  void introAgain(){
        Log.e(TAG, "introAgain: ");
        if(mCurrentTask!=null&&!mCurrentTask.isFinish()){
            Log.e(TAG, "introAgain: mCurrentTask!=null&&!mCurrentTask.isFinish()->"+mCurrentTask.getName());
            mCurrentTask.introAgain();
        }
    }

    @Override
    public void returnParkPoint() {
        if(mReturnParkPoint!=null){
            Log.e(TAG, "returnParkPoint: hasSetReturnPoint->"+mReturnParkPoint.toString());

            if(!isParking()) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ISayTask sayTask = new SayTaskImpl();
                sayTask.play(AppApplication.getContext().getString(R.string.tip_return_parking), new ITtsTaskCallback() {
                    @Override
                    public void onFinish(int code) {
                        setIsParking(true);

                        parkGo();
                    }
                });
            }else {
                parkGo();
            }
        }else {
            closeState();
        }
    }

    private void parkGo(){
        IGoTask goTask = new GoTaskImpl();
        goTask.go(mReturnParkPoint.getX(), mReturnParkPoint.getY(), mReturnParkPoint.getTheta(), goTaskCb);
    }

    private ITaskCallback goTaskCb=new ITaskCallback() {
        @Override
        public void onFinish() {
            setIsParking(false);

            //游览状态
            closeState();
        }
    };

    /**
     * 开始下一个游览点
     */
    public  IWayGuiderTask next(){
        return mQueue.peek();
    }

    private static boolean isParking=false;

    public static boolean isParking() {
        synchronized (WayGuiderManager.class) {
            return isParking;
        }
    }

    public static void setIsParking(boolean isParking) {
        synchronized (WayGuiderManager.class) {
            WayGuiderManager.isParking = isParking;
        }
    }

    private void closeState(){
        Event event=new Event();
        event.source=Event.EVENT_CMD_NAME_REALLY_END;
        NotificationCenter.defaultCenter().publish(event);
    }
}
