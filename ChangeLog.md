# NB进化史

## 2.0 "Merry Christmas"

大家昨晚都很累吧 ^_^

* 添加zookeeper集成,由[天空](https://github.com/tiankongkm)提供
* 添加Vecloity模板引擎,由[haoqoo](https://github.com/haoqoo)提供
* 修正了几个bug

* 时间: 2017-12-25
* 事件: 第一个正式版,GA级别
* 曲目: [Merry Christmas](https://www.youtube.com/watch?v=_f04x4Uu51A)

* 变更:
	* add: zkclient by [天空](https://github.com/tiankongkm)
	* add: Vecloity模板引擎 by [haoqoo](https://github.com/haoqoo)
	* add: daocache配置化
	* add: undertow 支持websocket by [qinerg](https://github.com/qinerg)
	* add: jetty 支持更详细的配置 by [haoqoo](https://github.com/haoqoo)
	* add: mongodb 支持集群配置 issue by [科技](https://github.com/Rekoe)
	* add: cxf 支持webservice
	* update: beetl升级到2.7.25
	* update: beetlsql升级到2.10.3
	* update: zbus升级到0.11.4
	* update: dubbo升级到2.5.8
	* update: tomcat升级到8.5.48
	* update: undertow升级到1.4.21-Final
	* remove: 移除配置项web.filters.order
	* fix: 清理pom.xml中的依赖关系
	* fix: beetl模板的自定义属性没有生效  issue by [道坤](https://github.com/albinhdk)

## 2.0-RC2 "My Love"

这一次,我们迎来了第三个web容器tomcat,由[@benjobs](https://github.com/wolfboys)完成,终于凑齐了!

* 更多NoSQL数据库? 新增mongodb by [@qingerg](https://github.com/qingerg)
* 分库分表? 现在支持sharding-jdbc
* 更多模板引擎,新增thymeleaf
* 其他ORM/SQL工具? 新增闲大赋的BeetlSQL
* 添加Spring桥接,完美支持U家三剑客(uflo工作流,ureport报表,urule规则引擎)

* 时间: 2017-12-14
* 事件: 第二个预备GA版本
* 曲目: [My Love](https://www.youtube.com/watch?v=ulOb9gIGGd0)

* 变更:
	* add: new NbApp()可以不传类名,从堆栈自动推断
	* add: starter-mongodb by [@qingerg](https://github.com/qingerg)
	* add: starter-tomcat by [@benjobs](https://github.com/wolfboys)
	* add: starter-beetlsql 来自beetl的SQL解决方案
	* add: starter-sharding-jdbc 分库分表
	* add: starter-thymeleaf 模板引擎
	* add: starter for U家三剑客(uflo工作流,ureport报表,urule规则引擎)
	* add: [控制NbApp的启停](https://gitee.com/nutz/nutzboot/issues/IGOE4)
	* add: starter-shiro支持多个realm by [科技](https://github.com/Rekoe)
	* add: starter-shiro改造RememberMeManager
	* fix: jetty扫描websocket的endpoint有问题
	* fix: 补全demo-maker的新增starter by "瞎折腾"
	* fix: [修改jetty默认超时设置](https://github.com/nutzam/nutz/issues/1365)

## 2.0-RC "属于"

首先,必须高亮一下[@qinerg](https://github.com/qinerg)提交的[starter-undertow](https://gitee.com/nutz/nutzboot/tree/dev/nutzboot-starter-undertow),嗷嗷嗷. Jetty与Undertow任意切换^_^

模板引擎([beetl](http://ibeetl.com/)和[jetx](http://subchen.github.io/jetbrick-template/))的starter已经完成,所以移除了starter-jetty的jsp依赖.

* 时间: 2017-12-01
* 事件: 第一个预备GA版本
* 曲目: [属于](https://www.bilibili.com/video/av5451358/)
* 变更:
	* add: 添加starter-undertow by [@qinerg](https://github.com/qinerg)
	* add: 添加starter-swagger及其demo
	* add: 支持命令行配置参数及profile
	* add: 支持打印配置文档
	* add: 添加beetl和jetx模板的starter
	* add: starter-jdbc支持HikariCP
	* add: 添加starter-ngrok,轻松获取外网调试URL
	* add: 添加目录规范文档
	* remove: starter-jetty默认不再添加jsp支持
	* change: starter-jetty添加nutz-plugins-websocket插件
	* fix: demo-maker的MainLauncher缺了init方法

## 2.0-Beta "天空之城"

听着董敏演奏的"天空之城",写着NB的代码,很惬意

* 时间: 2017-11-22
* 事件: 第一公测版
* 曲目: [Castle in the Sky](https://www.youtube.com/watch?v=wul6nubmJdU)
* 变更:
	* add: starter-zbus zbus-rpc相关
	* add: starter-dubbo dubbo相关
	* add: starter-shiro 权限基础集成
	* add: starter-quartz 计划任务
	* add: demo-zbus zbus-rpc的demo
	* add: demo-dubbo dubbo-rpc的demo
	* change: 打印系统启动耗时
	* fix: @煜 提醒说mina拼错成mima了

## 2.0-Preview 小荷才露尖尖角

* 时间: 2017-11-16
* 事件: 第一预览版
* 变更:
	* add: NutzBoot核心
	* add: starter-nutz-mvc NutMvc相关
	* add: starter-nutz-dao NutDao相关
	* add: starter-jdbc 数据库连接池相关
	* add: starter-jedis redis操作集成
	* add: starter-jetty servlet容器实现
	* add: starter-jedisque 潇潇同学提供的队列实现
	* add: starter-wkcache 大鲨鱼提供的方法缓存
	* add: starter-weixin 集成nutzwx方便微信公众号开发
	* add: demo-simple 展示最简单的mvc
	* add: demo-simple-dao 演示dao操作
	* add: demo-simple-redis 演示redis操作
	* add: 蛋蛋版banner打印器及"巨根"版banner

## 简称NB

* 时间: 2017-10-20
* 事件: 初始化项目库