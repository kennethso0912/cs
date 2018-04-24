## 文档索引

* [灯控服务&API设计文档](https://10.10.1.34/Rose/rosa_packages_services_LightService/blob/develop/%E7%81%AF%E6%8E%A7%E6%9C%8D%E5%8A%A1&API%E8%AE%BE%E8%AE%A1%E6%96%87%E6%A1%A3.md)
* [灯控服务子模块配置编译文档](https://10.10.1.34/Rose/rosa_packages_services_LightService/blob/develop/%E7%81%AF%E6%8E%A7%E6%9C%8D%E5%8A%A1%E5%85%B7%E4%BD%93%E4%BA%A7%E5%93%81%E6%A8%A1%E5%9D%97%E9%85%8D%E7%BD%AE%E7%BC%96%E8%AF%91%E6%96%87%E6%A1%A3.md)
* [灯控服务集成文档](https://10.10.1.34/Rose/rosa_packages_services_LightService/blob/develop/%E7%81%AF%E6%8E%A7%E6%9C%8D%E5%8A%A1%E9%9B%86%E6%88%90%E6%96%87%E6%A1%A3.md)

## 构建说明

由于灯控服务框架中支持多个产品的接入，多个硬件平台，所以编译特定产品的灯控服务，需指定产品硬件平台

* 构建alpha2灯控服务

```
    ./gradlew assembleAlpha2Release
```

* 构建cruzr灯控服务

```
   ./gradlew assembleCruzrRelease
```

* 构建mini灯控服务

```
    ./gradlew assembleMiniRelease
```

 **灯控服务SDK包上传Maven步骤：**  
1. 执行脚本生成jar  
   ```
    ./gradlew ：ledsdk:jarSdk
   ```  
2. 执行脚本上传jar到Maven私服：  
   release方式：  
    ```
    ./gradlew ：ledsdk:artifactoryPublish -Psnapshot=false
   ```  
   OR snapshot方式：  
   ```
    ./gradlew ：ledsdk:artifactoryPublish [-Psnapshot=true]
   ```
