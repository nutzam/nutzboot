# NB进化史

# 2.3.0.v20190220 "不可说"

地球在流浪,我们继续发新版^_^

* 添加start-sentinel-annotation 流量防卫原生注释版
* 使用nutz正式版1.r.67,不引用快照版

* 时间: 2019-02-20
* 曲目: [不可说](https://www.youtube.com/watch?v=gRd4iQhx9S8)
* 兼容性: 兼容2.0.x/2.1.x/2.2.x/2.3.x
* 变更:
	* add: starter-logback-exts logback日志扩展 by 大鲨鱼
	* update: 使用nutz 1.r.68.v20190220 例行更新
	* update: 使用nutzwx 1.r.68.v20190220 卡包API
	* update: 使用nutz插件集 1.r.68.v20190220 zbus/dubbo相关增强
	* update: 使用jetty最新版

# 2.2.9 "魔女の宅急便"

* 添加start-sentinel-dubbo 流量防卫 by 大鲨鱼
* 使用nutz正式版1.r.67,不引用快照版

* 时间: 2019-01-09
* 曲目: [魔女の宅急便](https://www.youtube.com/watch?v=jJJHH_v9JMg)
* 兼容性: 兼容2.0.x/2.1.x/2.2.x
* 变更:
	* add: start-sentinel-dubbo 流量防卫 by 大鲨鱼,已应用在nutzwk
	* update: 更新jetty/spring/jetx/beetl/undertow/druid版本 by howe
	* 使用nutz正式版1.r.67

# 2.2.8 "菊次郎的夏天"

本版本添加servicecomb支持,修正了几个bug

北半球的冬季, 南半球的夏天, ^_^

* 时间: 2018-12-14
* 曲目: [菊次郎的夏天](https://www.youtube.com/watch?v=4vQc8WC7clI)
* 兼容性: 兼容2.0.x/2.1.x/2.2.x
* 变更:
	* add: 添加servicecomb支持
	* update: 更新jetty/spring/jetx/beetl/undertow/druid版本 by howe
	* update: 更新javassist版本,解决jdk11兼容性问题 by [大鲨鱼](https://github.com/Wizzercn)
	* fix: fix:druid监控配置手册与实际过滤前缀不一致 by [realoldroot](https://github.com/realoldroot)
	* fix: ngrok client不能设置服务器地址 by wendal

# 2.2.7 "beautiful in white" 例行更新

本版本添加jetty的https支持

* 时间: 2018-11-08
* 曲目: [beautiful in white](https://www.youtube.com/watch?v=XRuDQ6aYeD0)
* 兼容性: 兼容2.0.x/2.1.x/2.2.x

* 变更:
	* add: jetty支持监听https端口
	* add: starter-lettuce by howe
	* fix: jetty禁用目录展示 


# 2.2.6 "nothing gonna change my love for you"

本版本强化多数据源支持,增加tcc支持

由于核心已稳定,所以更新速度稍有下降,引起部分基友的[疑惑](https://nutz.cn/yvr/t/7ef8uiffluh68qgqa1r9aceju9) ^_^,在此表示感谢

* 时间: 2018-09-19
* 曲目: [nothing gonna change my love for you](https://www.youtube.com/watch?v=dMIGKOxLZH8)
* 兼容性: 兼容2.0.x/2.1.x/2.2.x

* 变更:
	* update: tio升级到3.1.8.v20180818-RELEASE,减少依赖项
	* update: 更新jetty/tomcat/beetl by howe
	* add: tcc-transaction nutz 插件支持
	* update: 配置中心客户端逻辑更新 by wendal
	* update: 强化多数据源支持 by wentao
	* add: 添加actuator基础代码
	* add: 配置中心服务器端 https://gitee.com/nutz/nutzcloud-config-server

# 2.2.5 "心要野"

本版本主要是新增两个功能, 邮件客户端和防重复提交,均由[threefish](https://github.com/threefish) 提供^_^

* 时间: 2018-08-20
* 曲目: [心要野](https://music.163.com/#/song?id=406072138)
* 兼容性: 兼容2.0.x/2.1.x/2.2.x

* 变更:
	* add: starter-email 邮件客户端 by threefish
	* add: starter-prevent-duplicate-submit 防重复提交 by threefish
	* add: 在war打包模式下,支持warMain入口,解决自定义NbApp实例的需求

# 2.2.4 "BD7OEC"

CQ CQ CQ, this is BD7OEC, BRAVO DELTA 7 OSCAR ECHO CHARLIE, BD7OEC, calling CQ and standby.

* 时间: 2018-08-06
* 事件: "拿证了"
* 曲目: [业余无线电](https://www.bilibili.com/video/av21992170)
* 兼容性: 兼容2.0.x/2.1.x/2.2.x

* 变更:
	* add: [starter-nutz-dao支持多个不同的数据源](https://gitee.com/nutz/nutzboot/pulls/9) by [文涛](https://gitee.com/wentao0291)

# 2.2.3 "This Is Me"

修复shiro session默认过期时间,增强@PropDoc的可配置性

* 时间: 2018-07-25
* 曲目: [This Is Me](https://www.youtube.com/watch?v=CjxugyZCfuw)
* 兼容性: 兼容2.0.x/2.1.x/2.2.x
* 变更:
	* add: 添加配置项nutz.propdoc.packages可自定义扫描@PropDoc的路径
	* fix: shiro的默认session超时设置错误
	* update: 更新tio版本号
	* add: NbApp添加setMainPackage方法

# 2.2.2 "If You"

添加elasticsearch支持

* 时间: 2018-07-03
* 事件: "亚洲球队全部出局了"
* 曲目: [If You](https://www.youtube.com/watch?v=vUIHVpikGpI)
* 兼容性: 兼容2.0.x/2.1.x/2.2.x
* 变更:
	* add: starter-elasticsearch by 大鲨鱼
	* add: jst模板引擎,自带nutzboot支持 by wendal

# 2.2.1 "Live It Up"

使用nutz 1.r.66正式版

* 时间: 2018-06-15
* 事件: 世界杯第二天
* 曲目: [Live It Up](https://www.youtube.com/watch?v=V15BYnSr0P8)
* 兼容性: 与2.0.x/2.1.x完全兼容

* 变更:
	* update: 使用nutz 1.r.66正式版本
	* add: 添加配置项,支持禁用swagger

# 2.2.0 "荷塘月色"

完善serlvet容器(jetty/undertow/tomcat)对web.xml的替代性配置

* 时间: 2018-06-11
* 曲目: [荷塘月色](https://www.youtube.com/watch?v=nbWJv5KAIGY)
* 兼容性: 与2.0.x/2.1.x完全兼容

* 变更:
	* add: tomcat也需要支持404和500页面等页面 https://gitee.com/nutz/nutzboot/issues/IKC3B by [mm](https://gitee.com/aaabbbsdfasdfasdfasdfasf)
	* add: undertow也需要支持404和500页面等页面
	* add: 为jetty/undertow/tomcat添加welcome file配置
	* add: 支持获取undertow底层实例
	* add: WebServletFace添加getPathSpecs方法,可映射多个路径
	* add: WebServletFace和WebFilterFace添加setServletContext方法
	* add: [集成pageoffice的demo](https://gitee.com/nutz/nutzboot-demo-pageoffice)
	* change: 将spring支持单独为nutzboot-spring
	* update: dubbo更新到2.6.2 by 大鲨鱼
	* update: jetty更新到9.4.11.v20180605 by howe

# 2.1.9 "隐形的翅膀"

原生支持gzip压缩

* 时间: 2018-06-01
* 事件: 六月儿童节
* 曲目: [隐形的翅膀](https://www.youtube.com/watch?v=U9wOPpX0ZLo)
* 兼容性: 与2.0.x和2.1.x完全兼容

* 变更:
	* fix: 打包成war之后,[freemaker启动失败](https://nutz.cn/yvr/t/qcnfndh6tajknqqkgt5c5e186t)
	* add: undertow支持启用gzip, issue #138@github
	* add: jetty支持启用gzip
	* add: starter-tio支持ssl配置
	* update: asm升级到6.1.1,解决jdk10上可能报错的问题
	* update: 更新druid等依赖的版本 by howe

# 2.1.8 "这视频比歌好听"

统一web容器的核心逻辑,并支持war打包(终于填了这个坑)

* 时间: 2018-05-15
* 事件: 某未命名航母开始海试了
* 曲目: [这视频比歌好听](https://www.youtube.com/watch?v=ilmdOGbUuFk)
* 兼容性: 与2.0.x和2.1.x完全兼容

* 变更:
	* add: issue 36, Web服务类应共享一个超类
	* add: issue 60, Tomcat也要支持多层资源加载
	* add: 新增war打包方式,通过nutzboot-maven-plugin
	* add: 支持repo-upload和repo-download
	* add: jetty.port等端口统统支持随机设置,设置为0即可
	* add: tomcat线程池设置
	* add: 配置手册总表
	* add: 支持从ioc容器获取FilterChainResolver实例
	* fix: jetty缺了staticPath设置
	* fix: 补充WhaleFilterStarter的@PropDoc 
	* fix: 补充WeixinStarter的@PropDoc 
	* fix: 补充JedisStarter的@PropDoc
	* fix: tomcat端口被占用或者其他报错的时候,没有退出
	* fix: nutzwx版本号没跟上
	* update: 更新jetty/tomcat/beetl/beetlsql/tio的版本
	* change: nutzboot-demo-maker独立成[nutzboot-project-maker库](https://gitee.com/nutz/nutzboot-project-maker)
	
图片镇楼

![海试](http://5b0988e595225.cdn.sohucs.com/images/20180513/255815a8da954e6fa346adb388ed7dba.jpeg)

# 2.1.7 "Penguin's Game"

新增maven插件和mqtt支持

* 时间: 2018-05-04
* 事件: 青年节
* 曲目: [Penguin's Game](https://www.youtube.com/watch?v=WTJBB8r6lIE)
* 兼容性: 与2.0.x和2.1.x完全兼容

* 变更:
	* fix: 修改jetty热加载无效问题 by 蛋蛋
	* add: jetty支持任意状态码和任意异常类型的错误页面设置 by 蛋蛋 and wendal
	* add: 添加starter-mqtt-client by wendal
	* update: 更新所依赖的 jetx、cxf、spring、undertow 的版本 by 温泉

# 2.1.6 "A Million Dreams"

增强对新版dubbo的集成,以协助nutzwk的nutzboot-dubbo分支的线上部署.

* 时间: 2018-04-19/20,取决于你的经度
* 事件: 贸易战不好玩,看热闹的都不怕事大
* 曲目: [A Million Dreams](https://www.youtube.com/watch?v=g9r5PFZihC4) 歌曲从3:00开始
* 兼容性: 与2.0.x和2.1.x完全兼容

* 变更:
	
	* add: 支持无dubbo.xml配置
	* add: 支持以随机端口启动dubbo
	* add: 从ioc容器自动添加NbAppEventListener，并支持多个监听器
	* add: 支持druid的DruidPasswordCallback
	* add: 添加CounterService
	* update: 更新部分依赖的版本号
	* update: 测试与Oracle JDK 8/9/10的兼容性，无异常
	* update: 测试与OpenJDK 8/9/10的兼容性,无异常
	* fix: AppContext.stopServers的时候只应stop已经加载的ServerFace实例

* 后面的内容纯粹蹭热点,wendal闲来无事在发牢骚,请无视:

	- 曾经,自主创新/自主可控/自主知识产权变成贬义词,现在的画风似乎变了
	- 因为,"中兴"给了大家N多的茶余饭后的谈资
	- 做过微信公众平台的同学应该知道"Illegal key size or default parameters"错误
	- JDK8需要自行解除限制,不过JDK10已经默认无限制
	- 只是补充了一句: You are advised to consult your export/import control counsel or attorney to determine the exact requirements of your location, and what policy settings should be used.
	- 哪天"特没谱"说要限制的时候,微信服务器统统不合法-_-

# 2.1.5 "Je m'appelle Hélène"

原本是上周发新版的,但忙于[NutzCloud](https://gitee.com/nutz/nutzcloud)的开发,延期到本周发新版了.

本次更新带来单元测试支持及"道坤"提供的ssdb支持,修复了几个不算严重的issue,建议更新

* 时间: 2018-04-02
* 事件: 某飞行器以"无控坠落"的方式再入大气层
* 曲目: [Je m'appelle Hélène](https://www.youtube.com/watch?v=OrBjkXziXnw)
* 兼容性: 与2.0.x和2.1.x完全兼容

* 变更:

    * fix: dubbo的service如果引用QuarteManager会抛异常
    * fix: 修复wkcache在某些场景下失效的bug
    * update: 完善shiro集成, by 大鲨鱼
    * add: starter-ssdb by 道坤
    * add: 添加NbAppEventListener,可以拦截NbApp的各种事件
    * add: 添加单元测试的支持
    * add: shiro主动搜索ioc容器内的SessionListener并添加到SessionManager
    * add: jetty添加staticPathLocal属性,解决idea用户开发期js/css文件不更新的问题
    * add: jetty添加httpConfig配置项,可以更进行更精细的配置
    * change: 扩大server.port的随机范围
    
最后放个图片

![Tiangong-1](http://www.aerospace.org/CORDSuploads/TiangongStoryboard.png)

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
