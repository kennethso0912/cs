package com.ubtechinc.alphamini.factory;

import com.ubtechinc.alphamini.ILedController;
import com.ubtechinc.alphamini.MouthLedController;

/**
 * @desc : 灯控工厂类
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/13
 */
public class LedFactory {
     public static ILedController createFactory(int lId){
           switch (lId){
               // TODO 支持多种灯控，目前仅支持嘴巴灯
               case CmdOp.TYPE_EYE:
                   return MouthLedController.getInstance();
               case CmdOp.TYPE_HEAD:
                   return MouthLedController.getInstance();
               case CmdOp.TYPE_MOUTH:
                   return MouthLedController.getInstance();
           }
           return null;
     }
}