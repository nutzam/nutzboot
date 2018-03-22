# NB进化史

# dev 迭代中

* 变更:
    * update: 完善shiro集成, by 大鲨鱼
	* add: starter-ssdb by 道坤

# 2.1.4 "平凡之路"

修正一些bug,提升与spring项目的互操作性,2.1.x可平稳更新

* 时间: 2018-03-13
* 事件: 就想发个版本
* 曲目: [平凡之路](https://www.youtube.com/watch?v=NjTT5_RSkw4)

* 变更:
	* update: javassist更新为3.22.0-GA,解决"invalid constant type: 15"
	* update: 让beetl使用antlr4-runtime 4.5,解决与urule的jar冲突 [csl_slchia](https://gitee.com/csl_slchia)
	* add: 添加将普通web项目转换为NutzBoot项目的指南
	* add: 添加@AsSpringBean注解,在集成spring相关库(例如urule/ureport/uflo)的时候,可以主动把对象放入spring ioc
	* fix: jetty+shiro+websocket时,在onOpen阶段拿不到session对象
	* fix: urule集成后找不到自定义的@ActionBean对象 by [csl_slchia](https://gitee.com/csl_slchia)

# 2.1.3 "Never Enough"

炒币有风险,上车请谨慎. 区块链 != 炒币

人嘛, "Never Enough".

* 时间: 2018-02-26
* 事件: 新春来一发
* 曲目: [Never Enough](https://y.qq.com/n/yqq/mv/v/n0025krp84f.html)

* 变更:
	* add: starter-web3j 区块链-"以太坊"客户端API 
	* update: HikariCP更新到2.7.7
	* update: sharding-jdbc更新到2.0.3
	* update: dubbo更新到2.5.9
	* update: cxf更新到3.1.14
	* update: beetlsql更新到2.10.10
	* update: undertow更新到1.4.22.Final
	* update: tomcat更新到8.5.28
	* update: zbus更新到0.11.5
	* fix: undertow的staticPath就没生效 report by [道坤](https://github.com/albinhdk)和[tasdingoo](https://github.com/tasdingoo)

## 2.1.2 "いつも何度でも "

感谢tio社区为nutz特意发布的[2.0.8.v20180205-RELEASE](http://www.oschina.net/news/93135/t-io-2-08) ^_^

这次更新,带来
* [蛋蛋](https://github.com/TopCoderMyDream)完成的starter-freeamarker及
* 到处挖坑的[wendal](https://gitee.com/wendal)带来的starter-tio-mvc, 将ioc机制融入tio-mvc

这个版本完全兼容2.1.0/2.1.1,所以没有跳到2.2.0,请放心升级.

* 时间: 2018-02-06
* 事件: tio特别版
* 曲目: [いつも何度でも _千与千寻主题曲](https://www.youtube.com/watch?v=2zda1Tr4big)

-----------------------------------------------------------------------------

* 变更:
	* add: starter-tio-mvc 基于tio的高性能MVC框架
	* add: starter-freemarker by [蛋蛋](https://github.com/TopCoderMyDream)
	* update: 优化starter-tio的逻辑 by [zjSniper](https://gitee.com/zjSniper)
	* update: j2cache配置文件融入nutzboot的主配置信息 by [蛋蛋](https://github.com/TopCoderMyDream)
	* update: starter-beetl将GroupTemplate放入ioc容器,方便用户自定义和获取
	* update: 更新tio版本到2.0.8.v20180205-RELEASE, 感谢tio社区的大力支持
	* fix: 一主多从的逻辑有问题,修正之
	* fix: tomcat与shiro一起使用时报错, report by [蛋蛋](https://github.com/TopCoderMyDream)

## 2.1.1 "La Vie en Rose"

新增由[蛋蛋](https://github.com/TopCoderMyDream)提供的starter-tio和starter-j2cache, 温泉同学完成了eureka-server新版的status页面, 及2个配置中心的客户端 ^_^

这个版本完全兼容2.1.0,所以没有跳到2.2.0,请放心升级.

* 时间: 2018-01-26
* 事件: 春节前发个版本吧
* 曲目: [La Vie en Rose_玫瑰人生法语原声](https://www.youtube.com/watch?v=0feNVUwQA8U)

-----------------------------------------------------------------------------

* 变更:
	* add: starter-tio by [蛋蛋](https://github.com/TopCoderMyDream)
	* add: starter-j2cache by [蛋蛋](https://github.com/TopCoderMyDream)
	* add: starter-apollo-client 对接apollo配置中心. apollo是携程框架部门研发的分布式配置中心
	* add: starter-config-client NB配置中心的客户端,其服务端可以是任意支持Restful的服务器
	* add: feign支持从ioc容器获取client/encoder/decoder,且自定义JsonFormat
	* add: feign支持全局connect和read的timeout设置
	* add: starter-eureka-server新版的status页面 by [温泉](https://github.com/ywjno)
	* add: starter-jdbc添加slave数据源的支持,使得starter-nutz-dao和starter-beetlsql支持slave数据源
	* add: 从jar所在目录读取配置文件 by [文涛](https://github.com/swtseaman)
	* update: 更新HikariCP到2.7.5
	* update: 更新sharding-jdbc到2.0.2,终于支持建表语句了,所以dao.create也能工作了!
	* fix: tomcat的默认maxPostSize太小了,容易挂
	* fix: tio端口设置没有生效
	* fix: quartz没有对应的demo
	* fix: 兼容sfl4j-simple

## 2.1.0 "Start Of Something New"

新增和更新了一堆组件,推荐更新到这个版本

* 时间: 2018-01-10
* 事件: 2018年新的开始
* 曲目: [Start Of Something New](https://www.youtube.com/watch?v=I6EOUaWscrE)

-----------------------------------------------------------------------------


* 变更:
	* add: [caffeine](https://github.com/ben-manes/caffeine) 方法缓存 by [幸福的旁边](https://github.com/happyday517)
	* add: [hystrix](https://github.com/Netflix/Hystrix) 支持@HystrixCommand和/hystrix.stream
	* add: [hystrix-dashboard](https://github.com/Netflix/Hystrix) Hystrix的DashBoard
	* add: [eureka-server](https://github.com/Netflix/eureka) 服务治理的注册服务
	* add: [eureka-client](https://github.com/Netflix/eureka) 服务自动注册
	* add: [xxl-job-executor](https://github.com/xuxueli/xxl-job) 
	* add: feign完成feign-ribbon-eureka集成,实现负载均衡和服务发现
	* add: feign添加feign-hystrix依赖,强化与hystrix的集成
	* add: [rabbitmq](https://www.rabbitmq.com/) 开源消息代理
	* add: [activiti](https://www.activiti.org/) 工作流
	* change: feign默认不设置encoder/decoder,上一个版本默认jackson,并添加更多配置项.
	* change: 重构项目结构,用户项目不再需要依赖nutzboot-starter
	* change: 启用server.port和server.host,将jetty/undertow/tomcat的port和host设为过期配置,但继续兼容.
	* update: beetl更新到2.7.26
	* update: ureport2更新到2.2.4, 不再需要通过反射设置BuildinDatasource和ImageProvider列表
	* update: urule更新到2.1.4,可以注入FunctionDescriptor了
	* update: uflo更新到2.1.1
	* update: Swagger UI 更新到3.9.0
	* fix: U家三剑客的PropDoc文档没显示出来
	

## 2.0.1 "刚好遇见你"

在各位的支持和关照下, NutzBoot成为GVP(码云最有价值开源项目)了,这份荣耀属于大家 ^_^

本次更新,带来了feign, 由[haoqoo](https://github.com/haoqoo)和[wendal](https://github.com/wendal)共同完成

* 时间: 2017-12-30
* 事件: 2017年年底GVP纪念版
* 曲目: [刚好遇见你](https://www.youtube.com/watch?v=rMjWoJ5Ji3Y)

-----------------------------------------------------------------------------

* 变更
	* add: [feign](https://github.com/OpenFeign/feign) by [haoqoo](https://github.com/haoqoo) and [wendal](https://github.com/wendal)
	* add: ureport从nutz ioc读取BuildinDatasource和ImageProvider的对象,由"鱼夫"报告
	* add: NutFilterStarter新增ActionLoaderFace
	* update: 补全shiro的@ProcDoc文档 by 蛋蛋
	* update: 补全U家三剑客(uflo/urule/ureport)的@ProcDoc文档 by 蛋蛋
	* update: NutFilterStarter自动过滤所有Servlet声明
	* fix: ureport与nutz mvc一起使用时报SpringBean抽象错误,由"鱼夫"报告
	* fix: beetl模板在应该自动刷新 ,由"温泉"报告
	* fix: undertow与swagger的兼容性问题
	* fix: swagger可能乱码

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
