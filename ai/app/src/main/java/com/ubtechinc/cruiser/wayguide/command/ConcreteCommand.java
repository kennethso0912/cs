package com.ubtechinc.cruiser.wayguide.command;

/**
 * Created on 2017/11/3.
 *
 * @Author KennethSo
 * @Version 1.0.0
 * @Desc
 */
public class ConcreteCommand implements ICommand{
    private ICommandReceiver ir;

    public void injectAction(ICommandReceiver ir){
        this.ir=ir;
    }

    @Override
    public void execute() {
        this.ir.action();
    }
}