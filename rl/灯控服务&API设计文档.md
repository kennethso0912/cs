#Led灯控服务功能
* sdk灯效接口定义
* 定义ROSE总线的消息格式

##Led服务设计
####1、 灯控服务\总线\应用关系示意图

![Aaron Swartz](https://github.com/marklogg/images/blob/master/led1.png?raw=true)

说明：

* Robot Bus Service --总线服务进程
    
* Robot App  -- 机器人端应用进程

* Robot Led Service -- Led服务进程

####2、Led服务设计

* 内部组件图
	
	![Aaron Swartz](https://github.com/marklogg/images/blob/master/led_compoment.png?raw=true)
	
* sdk对外接口定义
	
	![Aaron Swartz](https://github.com/kennethso0912/source/blob/master/source/LedApi.png?raw=true)  
**Api描述：**
* **public void getList(final ResponseListener<LedWrapper.LedInfos> listener)**  
   说明：通过ResponseListener回调获取机器人的所具备的所有灯的信息集合LedInfos  
  例子如下：  
#### 
		LedApi.get().getList(new ResponseListener<LedWrapper.LedInfos>() {
	        @Override
	        public void onResponseSuccess(LedWrapper.LedInfos ledInfos) {
	            List<LedWrapper.LedInfo> list= ledInfos.getLedList();
	               for (LedWrapper.LedInfo led:list) {
	                   int id=led.getLId();                      //灯ID
                       List<Integer> effects=led.getEffectList();//灯效ID集合
	                   List<Integer> colors=led.getColorList();  //灯颜色集合
	                   List<Integer> brights=led.getBrightList();//灯亮度集合
				   }
			}

			@Override
            public void onFailure(int errCode, @Nullable String errMsg) {
                 //errCode错误码  errMsg错误信息
            }
        });
#### 

* **public void setOn(List<LedWrapper.Led> leds, final ResponseListener<Void> listener)**   
   说明：打开指定的灯Led包含属性有int id,int color颜色,int bright亮度,Map<string ,string> effect(若只是打开灯无其它灯效效果，effect可以不设置)  
   例子如下：  
####  
         //打开灯
         LedWrapper.Led led=LedWrapper.Led.newBuilder().setLId(ledID).setBright(1).setColor(1).build();  
        
        //根据具体产品的呼吸灯效指定各产品各自的灯效属性参数,各产品的服务实现自行获取解析
        Map<String,String> effects=new HashMap<>();
        effects.put("呼吸灯效ID名","1");
        effects.put("呼吸灯效灯亮时间","10");
        effects.put("呼吸灯效灯灭时间","10");
        effects.put("呼吸灯效其它属性名","呼吸灯效其它属性值");

        //打开带灯效的灯
        LedWrapper.Led ledWithBreath=LedWrapper.Led.newBuilder().setLId(TYPE_EYE).setBright(1).setColor(1).putAllEffect(effects).build();

        ArrayList<LedWrapper.Led> list=new ArrayList<>();
        list.add(led);
        list.add(ledWithBreath);

        LedApi.get().setOn(list, new ResponseListener<Void>() {
            @Override
            public void onResponseSuccess(Void aVoid) {
                //成功打开灯的回调
            }

            @Override
            public void onFailure(int errCode, @Nullable String errMsg) {
                //打开灯失败回调     errCode错误码 errMsg错误信息
            }
        });
####   
 
* **public void setOff(List<Integer> ledIds, final ResponseListener<Void> listener)**  
     说明：关闭一组指定ID的灯  ledIds:灯的ID集合  
     例子如下：  
####
        //要关闭的灯ID集合
        ArrayList<Integer> ids=new ArrayList<>();
        ids.add(id1);
        ids.add(id2);

        LedApi.get().setOff(ids, new ResponseListener<Void>() {
            @Override
            public void onResponseSuccess(Void aVoid) {
                //成功关闭灯的回调
            }

            @Override
            public void onFailure(int errCode, @Nullable String errMsg) {
                /关闭灯失败回调     errCode错误码 errMsg错误信息
            }
        });
####
   

	
####3、灯控服务接入总线

* 声明灯控服务

#####
 
	//在总线中声明
	<application>
		 <! LedService extends BusService>
        <service android:name=".LedService">
              <intent-filter>
                <!-- 声明 Action，固定为下述值 -->
                <action android:name="com.ubtrobot.servicebus.action.CALLABLE" />
                <!-- 声明应答，一个 <data> 对应一个调用 --> <!-- scheme:协议，固定为“call” --> <!-- host，类似于域名。区分应用与服务 -->
                <!-- “应用”host:固定为“ubtrobot.com” -->
                <!-- “服务”host:“服务名称” + “ubtrobot.com”，“${service-name}.ubutrobot.com” --> <!-- path，标识 host 下的一个调用 -->
                <data
                    android:host="led.ubtrobot.com"
                    android:path="/setOn"
                    android:scheme="call" />
                <data
                    android:host="led.ubtrobot.com"
                    android:path="/setOff"
                    android:scheme="call"/>
                <data
                    android:host="led.ubtrobot.com"
                    android:path="/getList"
                    android:scheme="call" />
            </intent-filter>
                ...


* 定义灯控服务消息格式

#####
	
	//响应消息
	syntax = "proto3"; 
	option java_package = "com.ubtrobot.led.protos" ; 
		
	//机器人本体具备的灯
    message LedInfo {
	    int32 lId = 1;//led id
	    repeated int32 effect=2;//灯效集合
	    repeated int32 color=3;  //颜色集合
	    repeated int32 bright=4;//亮度集合
    }
	
    //要关闭的led ID集合
	message LedIds{
      repeated int32 ids=1;
    }


    //用户组装后的led
    message Led{
	    int32 lId=1;                    //灯ID
	    int32 color=3;                  //灯颜色
	    int32 bright=4;                 //灯亮度
	    map <string ,string> effect=5;  //灯效模式集合
    }


    //用户要打开的led集合
    message Leds{
        repeated Led leds=1;
    }

    

    message LedInfos {
        repeated LedInfo led = 1;
    }
	
	
* 实现LedService

#####

	// 灯控服务实现
	public class LedService implements AbstractCallable {
		  
		
		 // 来自某个会话的应用的调用，处理并应答
	    @Call("/setOn") 
	    public void setOn(Request request, Responder responder) {
	       try {
	            LedWrapper.Leds leds= ProtoParam.from(request.getParam()).unpack(LedWrapper.Leds.class);
	            LedControlImpl.get().setOn(leds);
	            responder.respondSuccess();
           }catch (ProtoParam.InvalidProtoParamException ex){
                responder.respondFailure(CallGlobalCode.BAD_REQUEST,ex.getMessage());
           }
	    }
	    
	    
	    @Call("/setOff") // 注解内配置的调用路径，必须与 Manifest 中声明的一一匹配
	    public void setOff(Request request, Responder responder) {
	    	//关灯
	    	try {
	             LedWrapper.LedIds lIds = ProtoParam.from(request.getParam()).unpack( LedWrapper.LedIds.class);
	             LedControlImpl.get().setOff(lIds);
	             responder.respondSuccess();
            }catch (ProtoParam.InvalidProtoParamException ex){
                 responder.respondFailure(CallGlobalCode.BAD_REQUEST,ex.getMessage());
            }
	    }
	    	    
	     @Call(path="/getList")// 注解内配置的调用路径，必须与 Manifest 中声明的一一匹配
         public void getList(Request request, Responder responder){
      
	        try {
	            List<LedWrapper.LedInfo> ledInfosList= LedControlImpl.get().getList();
	            LedWrapper.LedInfos ledInfosProto=LedWrapper.LedInfos.newBuilder().addAllLed(ledInfosList).build();
	            responder.respondSuccess(ProtoParam.pack(ledInfosProto));
	        }catch (Exception e){
	            e.printStackTrace();
	            responder.respondFailure(CallGlobalCode.INTERNAL_ERROR, "call fail : " + e.getMessage());
	        }
    }
	    
	    // 总线服务创建回调，用于初始化
	    @Override
	    public void onCallableCreate() {
	    	LedControlImpl.get().init();
	    }
	    
	    // 总线服务销毁回调，释放资源、结束任务后，退出整个程序
	    @Override 
	    public void onCallableDestroy() {
	    	LedControlImpl.get().onDestroy();
	    }
	}