# NutzBoot的项目结构

## 依赖管理
用NutzBoot做的项目,可以是maven项目,也可以是gradle项目. 

## 目录结构

NutzBoot有强制性的目录规范,请看下图

```
- pom.xml // 必选
- src
    - main
        - java
            - io
                - nutz
                    - demo // package必须大于等于2层,将MainLaucher放在根目录或者一级package是不允许的
                        - MainLauncher.java // 主启动类有main方法,但不一定叫MainLauncher
                        - MainSetup.java // 通常是nutz.mvc项目才需要
                        - bean // 存放Pojo类
                            - User.java
                            - Todo.java
                        - service // 服务类,dubbo/zbus等rpc服务默认从这个package扫描
                            - UserService.java // 接口类
                            - impl // 推荐将实现类放入单独的package
                                - UserServiceImpl.java  // 实现类
                        - module // 非强制,非必须
                            - UserModule.java
                            - BlogModule.java
        - resources
            - application.properties // 主配置信息文件
            - application-prod.properties // 不同profile的配置信息文件
            - application-docker.properties // 不同profile的配置信息文件
            - log4j.properties // 日志配置文件
            - custom // 个性化配置信息文件
                - xxx.properties
                - yyy.properties
            - locales // 国际化/本地化/i18消息文件存在目录
                - zh_CN
                    - user.properties
                    - sysadmin.properties
                - en_EN
                    - user.properties
                    - sysadmin.properties
            - ioc // 存放自定义的ioc js配置文件
                - upload.js
            - sqls // 存放自定义SQL文件
                - users.sql
                - jobs.sql
            - static  // 静态文件目录,例如js/css/html文件,目录结构不做强制要求
                        // 例如http://地址/rs/js/jquery.js可直接访问到jquery.js
                - index.html
                - rs
                    - js
                        - jquery.js
                        - vue.js
                    - css
                        - boot.min.css
            - webapp // 普通JavaEE项目的存放目录
                - WEB-INF
                    - web.xml // 绝大多数情况下不需要这个文件
            - template // 模版文件目录,模版类starter均从这个目录加载
                - index.jetx // jetx模版文件
                - index.btl // beetl模版文件
    - test
        - java
        - resources
```

除 `pom.xml`,`MainLauncher.java`,`application.properties`之外的所有文件均为可选.

再次提醒一下,MainLauncher这个名字不是强制要求