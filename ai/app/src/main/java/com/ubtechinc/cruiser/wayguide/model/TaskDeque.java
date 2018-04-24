package com.ubtechinc.cruiser.wayguide.model;

import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2017/3/4.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Des 任务队列
 */

class TaskDeque<T>{
    TaskDeque(){}

    private BlockingDeque<T> mQueue=new LinkedBlockingDeque<>();

    public  void enqueue(T task) {
        mQueue.add(task);
    }

    void enqueue(ArrayList<T> tasks){
        mQueue.addAll(tasks);
    }

    public  T poll(){
        try {
            return mQueue.poll(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return  null;
        }
    }

    T peek(){
       return mQueue.peek();
    }

    public TaskDeque<T> newQueue(){
        return new TaskDeque<>();
    }

    boolean remove(T t){
       return  mQueue.remove(t);
    }

    void addFirst(T t){
        mQueue.addFirst(t);
    }

    /**
     * 判断队列是否为空
     * @return
     */
    boolean isEmpty(){
        return mQueue.size()==0;
    }

    /**
     * 清空队列
     */
    void clear(){
        mQueue.clear();
    }

    int size(){
        return mQueue.size();
    }

    boolean contains(T t){
        return mQueue.contains(t);
    }

}
