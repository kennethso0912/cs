package com.ubtechinc.cruiser.wayguide.command;

/**
 * Created on 2017/11/3.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class Invoker {
    private ICommand command;
    public void action(){
        this.command.execute();
    }
}