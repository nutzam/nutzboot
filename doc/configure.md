# 配置总表

更新时间: 2018.05.10

|id  |key                                     |required  |Possible Values     |Default   |Description         |                                starters|
|----|----------------------------------------|----------|--------------------|----------|--------------------|----------------------------------------|
|0   |beetl.RESOURCE.autoCheck                |no        |                    |true      |自动检测模板更新            |org.nutz.boot.starter.beetl.BeetlGroupTemplateStarter|
|1   |beetl.RESOURCE.root                     |no        |                    |template/ |模板目录的路径             |org.nutz.boot.starter.beetl.BeetlGroupTemplateStarter|
|2   |beetl.RESOURCE.rootLocal                |no        |                    |          |模板目录的绝对路径,若不存在,回落到'模板目录的路径'|org.nutz.boot.starter.beetl.BeetlGroupTemplateStarter|
|3   |druid.web.filter.exclusions             |no        |                    |          |需要排除的路径             |org.nutz.boot.starter.jdbc.DruidWebStatFilterStarter|
|4   |druid.web.filter.principalCookieName    |no        |                    |          |用户权限信息的cookie属性名称   |org.nutz.boot.starter.jdbc.DruidWebStatFilterStarter|
|5   |druid.web.filter.principalSessionName   |no        |                    |          |用户权限信息的session属性名称  |org.nutz.boot.starter.jdbc.DruidWebStatFilterStarter|
|6   |druid.web.filter.profileEnable          |no        |                    |          |是否开启性能监控            |org.nutz.boot.starter.jdbc.DruidWebStatFilterStarter|
|7   |druid.web.filter.realIpHeader           |no        |                    |          |Header中ReadIp对应的key |org.nutz.boot.starter.jdbc.DruidWebStatFilterStarter|
|8   |druid.web.filter.sessionStatEnable      |no        |                    |true      |是否开启session状态监控     |org.nutz.boot.starter.jdbc.DruidWebStatFilterStarter|
|9   |druid.web.filter.sessionStatMaxCount    |no        |                    |          |session最大状态数量       |org.nutz.boot.starter.jdbc.DruidWebStatFilterStarter|
|10  |druid.web.servlet.allow                 |no        |                    |          |允许访问的ip列表           |org.nutz.boot.starter.jdbc.DruidWebStatServletStarter|
|11  |druid.web.servlet.deny                  |no        |                    |          |禁止访问的ip列表           |org.nutz.boot.starter.jdbc.DruidWebStatServletStarter|
|12  |druid.web.servlet.jmxPassword           |no        |                    |          |JMX的密码              |org.nutz.boot.starter.jdbc.DruidWebStatServletStarter|
|13  |druid.web.servlet.jmxUrl                |no        |                    |          |读取JMX信息的URL         |org.nutz.boot.starter.jdbc.DruidWebStatServletStarter|
|14  |druid.web.servlet.jmxUsername           |no        |                    |          |JMX的用户名             |org.nutz.boot.starter.jdbc.DruidWebStatServletStarter|
|15  |druid.web.servlet.loginPassword         |no        |                    |随机值,打印在日志中|访问monitor页面的密码      |org.nutz.boot.starter.jdbc.DruidWebStatServletStarter|
|16  |druid.web.servlet.loginUsername         |no        |                    |driud     |访问monitor页面的用户名     |org.nutz.boot.starter.jdbc.DruidWebStatServletStarter|
|17  |druid.web.servlet.resetEnable           |no        |                    |true      |是否允许重置统计结果          |org.nutz.boot.starter.jdbc.DruidWebStatServletStarter|
|18  |freemarker.suffix                       |no        |                    |.html     |文件后缀                |org.nutz.boot.starter.freemarker.FreemarkerViewMaker|
|19  |j2cache.L1.provider_class               |no        |                    |          |L1缓存提供者,可以有:none,ehcache,ehcache3,caffeine,redis|org.nutz.boot.starter.j2cache.J2cacheStarter|
|20  |j2cache.L2.provider_class               |no        |                    |          |L2缓存提供者,可以有:none,ehcache,ehcache3,caffeine,redis|org.nutz.boot.starter.j2cache.J2cacheStarter|
|21  |j2cache.broadcast                       |no        |                    |          |广播类型,例如redis        |org.nutz.boot.starter.j2cache.J2cacheStarter|
|22  |j2cache.caffeine.properties             |no        |                    |          |Caffeine的配置文件       |org.nutz.boot.starter.j2cache.J2cacheStarter|
|23  |j2cache.caffeine.region.default         |no        |                    |          |Caffeine的配置,参考:1000,1h |org.nutz.boot.starter.j2cache.J2cacheStarter|
|24  |j2cache.ehcache.configXml               |no        |                    |          |Ehcache的配置文件路径      |org.nutz.boot.starter.j2cache.J2cacheStarter|
|25  |j2cache.ehcache3.configXml              |no        |                    |          |Ehcache3的配置文件路径     |org.nutz.boot.starter.j2cache.J2cacheStarter|
|26  |j2cache.ehcache3.defaultHeapSize        |no        |                    |          |Ehcache3的缓存文件大小     |org.nutz.boot.starter.j2cache.J2cacheStarter|
|27  |j2cache.jgroups.channel.name            |no        |                    |          |组播名                 |org.nutz.boot.starter.j2cache.J2cacheStarter|
|28  |j2cache.jgroups.configXml               |no        |                    |          |Jgroups的配置文件        |org.nutz.boot.starter.j2cache.J2cacheStarter|
|29  |j2cache.redis.blockWhenExhausted        |no        |                    |          |连接耗尽时是否阻塞,如false则报异常,如ture则阻塞直到超时|org.nutz.boot.starter.j2cache.J2cacheStarter|
|30  |j2cache.redis.channel                   |no        |                    |          |Redis的Channel的名字    |org.nutz.boot.starter.j2cache.J2cacheStarter|
|31  |j2cache.redis.channel.host              |no        |                    |          |Redis的Channel的主机    |org.nutz.boot.starter.j2cache.J2cacheStarter|
|32  |j2cache.redis.cluster_name              |no        |                    |          |Redis的集群名称          |org.nutz.boot.starter.j2cache.J2cacheStarter|
|33  |j2cache.redis.database                  |no        |                    |          |Redis的可用数据库数        |org.nutz.boot.starter.j2cache.J2cacheStarter|
|34  |j2cache.redis.hosts                     |no        |                    |          |Redis主机名(包含端口)      |org.nutz.boot.starter.j2cache.J2cacheStarter|
|35  |j2cache.redis.lifo                      |no        |                    |          |是否启用后进先出            |org.nutz.boot.starter.j2cache.J2cacheStarter|
|36  |j2cache.redis.maxIdle                   |no        |                    |          |最大空闲连接数             |org.nutz.boot.starter.j2cache.J2cacheStarter|
|37  |j2cache.redis.maxTotal                  |no        |                    |          |最大连接数               |org.nutz.boot.starter.j2cache.J2cacheStarter|
|38  |j2cache.redis.maxWaitMillis             |no        |                    |          |获取连接时的最大等待毫秒数       |org.nutz.boot.starter.j2cache.J2cacheStarter|
|39  |j2cache.redis.minEvictableIdleTimeMillis|no        |                    |          |逐出连接的最小空闲时间         |org.nutz.boot.starter.j2cache.J2cacheStarter|
|40  |j2cache.redis.minIdle                   |no        |                    |          |最小空闲连接数             |org.nutz.boot.starter.j2cache.J2cacheStarter|
|41  |j2cache.redis.mode                      |no        |                    |          |Redis的mode,可以有:single(single redis server),sentinel(master-slaves servers),cluster(cluster servers),sharded(sharded servers)|org.nutz.boot.starter.j2cache.J2cacheStarter|
|42  |j2cache.redis.namespace                 |no        |                    |j2cache   |Redis的命名空间          |org.nutz.boot.starter.j2cache.J2cacheStarter|
|43  |j2cache.redis.numTestsPerEvictionRun    |no        |                    |          |每次逐出检查时,逐出的最大数目     |org.nutz.boot.starter.j2cache.J2cacheStarter|
|44  |j2cache.redis.password                  |no        |                    |          |Redis连接密码           |org.nutz.boot.starter.j2cache.J2cacheStarter|
|45  |j2cache.redis.softMinEvictableIdleTimeMillis|no        |                    |          |对象空闲多久后逐出           |org.nutz.boot.starter.j2cache.J2cacheStarter|
|46  |j2cache.redis.storage                   |no        |                    |          |Redis的存储mode,可以有:generic,hash|org.nutz.boot.starter.j2cache.J2cacheStarter|
|47  |j2cache.redis.testOnBorrow              |no        |                    |          |在获取连接的时候是否检查有效性     |org.nutz.boot.starter.j2cache.J2cacheStarter|
|48  |j2cache.redis.testOnReturn              |no        |                    |          |在return给pool时，是否提前进行validate操作；|org.nutz.boot.starter.j2cache.J2cacheStarter|
|49  |j2cache.redis.testWhileIdle             |no        |                    |          |在空闲时是否检查有效性         |org.nutz.boot.starter.j2cache.J2cacheStarter|
|50  |j2cache.redis.timeBetweenEvictionRunsMillis|no        |                    |          |逐出扫描的时间间隔(毫秒)       |org.nutz.boot.starter.j2cache.J2cacheStarter|
|51  |j2cache.redis.timeout                   |no        |                    |          |Redis连接超时时间         |org.nutz.boot.starter.j2cache.J2cacheStarter|
|52  |j2cache.serialization                   |no        |                    |          |序列化类型,可以有:fst(fast-serialization),kyro(kyro),java(java standard)|org.nutz.boot.starter.j2cache.J2cacheStarter|
|53  |jetty.contextPath                       |no        |                    |/         |上下文路径               |org.nutz.boot.starter.jetty.JettyStarter|
|54  |jetty.host                              |no        |                    |0.0.0.0   |监听的ip地址             |org.nutz.boot.starter.jetty.JettyStarter|
|55  |jetty.http.idleTimeout                  |no        |                    |300000    |空闲时间,单位毫秒           |org.nutz.boot.starter.jetty.JettyStarter|
|56  |jetty.httpConfig.blockingTimeout        |no        |                    |-1        |阻塞超时                |org.nutz.boot.starter.jetty.JettyStarter|
|57  |jetty.httpConfig.headerCacheSize        |no        |                    |8192      |头部缓冲区大小             |org.nutz.boot.starter.jetty.JettyStarter|
|58  |jetty.httpConfig.maxErrorDispatches     |no        |                    |10        |最大错误重定向次数           |org.nutz.boot.starter.jetty.JettyStarter|
|59  |jetty.httpConfig.outputAggregationSize  |no        |                    |8192      |输出聚合大小              |org.nutz.boot.starter.jetty.JettyStarter|
|60  |jetty.httpConfig.outputBufferSize       |no        |                    |32768     |输出缓冲区大小             |org.nutz.boot.starter.jetty.JettyStarter|
|61  |jetty.httpConfig.persistentConnectionsEnabled|no        |                    |true      |是否启用持久化连接           |org.nutz.boot.starter.jetty.JettyStarter|
|62  |jetty.httpConfig.requestHeaderSize      |no        |                    |8192      |请求的头部最大值            |org.nutz.boot.starter.jetty.JettyStarter|
|63  |jetty.httpConfig.responseHeaderSize     |no        |                    |8192      |响应的头部最大值            |org.nutz.boot.starter.jetty.JettyStarter|
|64  |jetty.httpConfig.securePort             |no        |                    |          |安全协议的端口,例如8443      |org.nutz.boot.starter.jetty.JettyStarter|
|65  |jetty.httpConfig.secureScheme           |no        |                    |          |安全协议,例如https        |org.nutz.boot.starter.jetty.JettyStarter|
|66  |jetty.httpConfig.sendDateHeader         |no        |                    |true      |是否发送日期信息            |org.nutz.boot.starter.jetty.JettyStarter|
|67  |jetty.httpConfig.sendServerVersion      |no        |                    |true      |是否发送jetty版本号        |org.nutz.boot.starter.jetty.JettyStarter|
|68  |jetty.maxFormContentSize                |no        |                    |1gb       |表单最大尺寸              |org.nutz.boot.starter.jetty.JettyStarter|
|69  |jetty.page.404                          |no        |                    |          |自定义404页面,同理,其他状态码也是支持的|org.nutz.boot.starter.jetty.JettyStarter|
|70  |jetty.page.java.lang.Throwable          |no        |                    |          |自定义java.lang.Throwable页面,同理,其他异常也支持|org.nutz.boot.starter.jetty.JettyStarter|
|71  |jetty.port                              |no        |                    |8080      |监听的端口               |org.nutz.boot.starter.jetty.JettyStarter|
|72  |jetty.staticPath                        |no        |                    |          |额外的静态文件路径           |org.nutz.boot.starter.jetty.JettyStarter|
|73  |jetty.staticPathLocal                   |no        |                    |          |静态文件所在的本地路径         |org.nutz.boot.starter.jetty.JettyStarter|
|74  |jetty.threadpool.idleTimeout            |no        |                    |60000     |线程池idleTimeout，单位毫秒 |org.nutz.boot.starter.jetty.JettyStarter|
|75  |jetty.threadpool.maxThreads             |no        |                    |500       |线程池最大线程数maxThreads  |org.nutz.boot.starter.jetty.JettyStarter|
|76  |jetty.threadpool.minThreads             |no        |                    |200       |线程池最小线程数minThreads  |org.nutz.boot.starter.jetty.JettyStarter|
|77  |mqtt.client.clientId                    |no        |                    |MqttClient.generateClientId()|客户端id               |org.nutz.boot.starter.mqtt.client.MqttClientStarter|
|78  |mqtt.client.connectOnStart              |no        |                    |true      |启动时自动连接             |org.nutz.boot.starter.mqtt.client.MqttClientStarter|
|79  |mqtt.client.options.automaticReconnect  |no        |                    |true      |自动重连                |org.nutz.boot.starter.mqtt.client.MqttClientStarter|
|80  |mqtt.client.options.cleanSession        |no        |                    |true      |清除session           |org.nutz.boot.starter.mqtt.client.MqttClientStarter|
|81  |mqtt.client.options.connectionTimeout   |no        |                    |30        |连接超时设置              |org.nutz.boot.starter.mqtt.client.MqttClientStarter|
|82  |mqtt.client.options.keepAliveInterval   |no        |                    |60        |心跳频率,单位秒            |org.nutz.boot.starter.mqtt.client.MqttClientStarter|
|83  |mqtt.client.options.password            |no        |                    |          |密码                  |org.nutz.boot.starter.mqtt.client.MqttClientStarter|
|84  |mqtt.client.options.urls                |no        |                    |          |多服务器地址设置            |org.nutz.boot.starter.mqtt.client.MqttClientStarter|
|85  |mqtt.client.options.username            |no        |                    |          |用户名                 |org.nutz.boot.starter.mqtt.client.MqttClientStarter|
|86  |mqtt.client.options.will.payload        |no        |                    |          |Will消息的内容           |org.nutz.boot.starter.mqtt.client.MqttClientStarter|
|87  |mqtt.client.options.will.qos            |no        |                    |2         |Will消息的QOS          |org.nutz.boot.starter.mqtt.client.MqttClientStarter|
|88  |mqtt.client.options.will.retained       |no        |                    |true      |Will消息是否retained    |org.nutz.boot.starter.mqtt.client.MqttClientStarter|
|89  |mqtt.client.options.will.topic          |no        |                    |          |Will消息的topic        |org.nutz.boot.starter.mqtt.client.MqttClientStarter|
|90  |mqtt.client.timeToWait                  |no        |                    |-1        |同步客户端的最大等待时间        |org.nutz.boot.starter.mqtt.client.MqttClientStarter|
|91  |mqtt.client.url                         |no        |                    |tcp://127.0.0.1:1883|服务器地址               |org.nutz.boot.starter.mqtt.client.MqttClientStarter|
|92  |nutz.dao.interceptor.cache.enable       |no        |                    |false     |是否使用daocache        |org.nutz.boot.starter.nutz.dao.NutDaoStarter|
|93  |nutz.dao.interceptor.log.enable         |no        |                    |true      |是否打印dao的SQL日志       |org.nutz.boot.starter.nutz.dao.NutDaoStarter|
|94  |nutz.dao.interceptor.time.enable        |no        |                    |false     |是否打印dao的SQL耗时日志     |org.nutz.boot.starter.nutz.dao.NutDaoStarter|
|95  |nutz.dao.sqls.path                      |no        |                    |sqls/     |sql目录               |org.nutz.boot.starter.nutz.dao.NutDaoStarter|
|96  |nutz.mvc.whale.enc.input                |no        |                    |UTF-8     |在其他Filter之前设置input编码|org.nutz.boot.starter.nutz.mvc.WhaleFilterStarter|
|97  |nutz.mvc.whale.enc.output               |no        |                    |UTF-8     |在其他Filter之前设置output编码|org.nutz.boot.starter.nutz.mvc.WhaleFilterStarter|
|98  |nutz.mvc.whale.http.hidden_method_param |no        |                    |          |隐形http方法参数转换所对应的参数名 |org.nutz.boot.starter.nutz.mvc.WhaleFilterStarter|
|99  |nutz.mvc.whale.http.method_override     |no        |                    |false     |是否允许使用X-HTTP-Method-Override|org.nutz.boot.starter.nutz.mvc.WhaleFilterStarter|
|100 |nutz.mvc.whale.upload.enable            |no        |                    |false     |是否启用隐形Upload支持      |org.nutz.boot.starter.nutz.mvc.WhaleFilterStarter|
|101 |shiro.ini.path                          |no        |                    |          |shiro.ini的路径,如果shiro.ini存在,就会使用它,否则走NB的内部逻辑|org.nutz.boot.starter.shiro.ShiroEnvStarter|
|102 |shiro.ini.urls                          |no        |                    |          |urls过滤清单            |org.nutz.boot.starter.shiro.ShiroEnvStarter|
|103 |shiro.realm.cache.enable                |no        |                    |          |realm是否缓存           |org.nutz.boot.starter.shiro.ShiroEnvStarter|
|104 |shiro.session.cache.redis.debug         |no        |                    |false     |session持久化时redis的debug模式|org.nutz.boot.starter.shiro.ShiroEnvStarter|
|105 |shiro.session.cache.redis.mode          |no        |                    |kv        |设置redis缓存的模式        |org.nutz.boot.starter.shiro.ShiroEnvStarter|
|106 |shiro.session.cache.redis.ttl           |no        |                    |-1        |redis缓存的过期时间        |org.nutz.boot.starter.shiro.ShiroEnvStarter|
|107 |shiro.session.cache.type                |no        |                    |memory    |设置使用的缓存类型           |org.nutz.boot.starter.shiro.ShiroEnvStarter|
|108 |shiro.session.cookie.httpOnly           |no        |                    |true      |Cookie是否只读          |org.nutz.boot.starter.shiro.ShiroEnvStarter|
|109 |shiro.session.cookie.maxAge             |no        |                    |946080000 |Cookie的过期时间,单位:毫秒   |org.nutz.boot.starter.shiro.ShiroEnvStarter|
|110 |shiro.session.cookie.name               |no        |                    |sid       |Cookie的name         |org.nutz.boot.starter.shiro.ShiroEnvStarter|
|111 |shiro.session.enable                    |no        |                    |true      |是否启用Shiro的Session管理 |org.nutz.boot.starter.shiro.ShiroEnvStarter|
|112 |shiro.url.login                         |no        |                    |/user/login|默认登录路径              |org.nutz.boot.starter.shiro.ShiroEnvStarter|
|113 |shiro.url.logout_redirect               |no        |                    |/         |退出登录后的重定向路径         |org.nutz.boot.starter.shiro.ShiroEnvStarter|
|114 |shiro.url.unauth                        |no        |                    |/user/login|访问未授权页面后的重定向路径      |org.nutz.boot.starter.shiro.ShiroEnvStarter|
|115 |ssdb.host                               |no        |                    |127.0.0.1 |SSDB的服务地址           |  org.nutz.boot.starter.ssdb.SsdbStarter|
|116 |ssdb.maxActive                          |no        |                    |10        |SSDB的最大连接数          |  org.nutz.boot.starter.ssdb.SsdbStarter|
|117 |ssdb.port                               |no        |                    |8888      |SSDB的服务端口           |  org.nutz.boot.starter.ssdb.SsdbStarter|
|118 |ssdb.testWhileIdle                      |no        |                    |true      |SSDB是否在空闲时检测链接存活    |  org.nutz.boot.starter.ssdb.SsdbStarter|
|119 |ssdb.timeout                            |no        |                    |2000      |SSDB的服务超时时间         |  org.nutz.boot.starter.ssdb.SsdbStarter|
|120 |tio.heartbeat                           |no        |                    |false     |是否启动框架层面心跳          |org.nutz.boot.starter.tio.server.TioServerStarter|
|121 |tio.heartbeatTimeout                    |no        |                    |120000    |心跳超时时间(单位:毫秒)       |org.nutz.boot.starter.tio.server.TioServerStarter|
|122 |tio.host                                |no        |                    |0.0.0.0   |tio监听的ip            |org.nutz.boot.starter.tio.server.TioServerStarter|
|123 |tio.name                                |no        |                    |NutzBoot GroupContext|上下文名称               |org.nutz.boot.starter.tio.server.TioServerStarter|
|124 |tio.port                                |no        |                    |9420      |tio监听端口             |org.nutz.boot.starter.tio.server.TioServerStarter|
|125 |tio_mvc.allowDomains                    |no        |                    |          |允许访问的域名,逗号分隔        |org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|126 |tio_mvc.apiInterceptor                  |no        |                    |apiInterceptor|拦截器                 |org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|127 |tio_mvc.charset                         |no        |                    |UTF-8     |字符集                 |org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|128 |tio_mvc.contextPath                     |no        |                    |          |tio mvc上下文路径        |org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|129 |tio_mvc.host                            |no        |                    |0.0.0.0   |tio监听ip/主机名         |org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|130 |tio_mvc.maxLiveTimeOfStaticRes          |no        |                    |          |maxLiveTimeOfStaticRes设置|org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|131 |tio_mvc.page404                         |no        |                    |/404.html |404页面               |org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|132 |tio_mvc.page500                         |no        |                    |/500.html |500页面               |org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|133 |tio_mvc.port                            |no        |                    |8080      |tio监听端口             |org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|134 |tio_mvc.serverInfo                      |no        |                    |          |服务器信息               |org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|135 |tio_mvc.sessionCacheName                |no        |                    |          |会话缓存的名称             |org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|136 |tio_mvc.sessionCookieName               |no        |                    |          |会话cookie的名字         |org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|137 |tio_mvc.sessionIdGenerator              |no        |                    |sessionIdGenerator|会话id生成器             |org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|138 |tio_mvc.sessionStore                    |no        |                    |sessionStore|会话id缓存提供者           |org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|139 |tio_mvc.sessionTimeout                  |no        |                    |1800      |tio mvc会话超时时间       |org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|140 |tio_mvc.suffix                          |no        |                    |          |默认后缀                |org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|141 |tio_mvc.useSession                      |no        |                    |true      |是否使用Session机制       |org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|142 |tio_mvc.welcomeFile                     |no        |                    |index.html|默认Welcome File      |org.nutz.boot.starter.tio.mvc.TioMvcHttpServerBeans|
|143 |tomcat.contextPath                      |no        |                    |          |上下文路径               |org.nutz.boot.starter.tomcat.TomcatStarter|
|144 |tomcat.host                             |no        |                    |0.0.0.0   |监听的ip地址             |org.nutz.boot.starter.tomcat.TomcatStarter|
|145 |tomcat.maxPostSize                      |no        |                    |64 * 1024 * 1024|POST表单最大尺寸          |org.nutz.boot.starter.tomcat.TomcatStarter|
|146 |tomcat.port                             |no        |                    |8080      |监听的端口               |org.nutz.boot.starter.tomcat.TomcatStarter|
|147 |tomcat.staticPath                       |no        |                    |static    |静态文件路径              |org.nutz.boot.starter.tomcat.TomcatStarter|
|148 |tomcat.staticPathLocal                  |no        |                    |          |本地静态文件路径            |org.nutz.boot.starter.tomcat.TomcatStarter|
|149 |undertow.contextPath                    |no        |                    |/         |上下文路径               |org.nutz.boot.starter.undertow.UndertowStarter|
|150 |undertow.host                           |no        |                    |0.0.0.0   |监听的ip地址             |org.nutz.boot.starter.undertow.UndertowStarter|
|151 |undertow.port                           |no        |                    |8080      |监听的端口               |org.nutz.boot.starter.undertow.UndertowStarter|
|152 |undertow.staticPath                     |no        |                    |static/   |静态文件路径              |org.nutz.boot.starter.undertow.UndertowStarter|
|153 |web.session.timeout                     |no        |                    |30        |Session空闲时间,单位分钟    |org.nutz.boot.starter.jetty.JettyStarter,org.nutz.boot.starter.tomcat.TomcatStarter,org.nutz.boot.starter.undertow.UndertowStarter|
|154 |web3j.http.debug                        |no        |                    |true      |http调试模式,显示通信内容     | org.nutz.boot.starter.web3.Web3jStarter|
|155 |web3j.http.includeRawResponses          |no        |                    |false     |http是否保留原始响应的内容     | org.nutz.boot.starter.web3.Web3jStarter|
|156 |web3j.http.url                          |no        |                    |http://localhost:8545/|以太坊节点URL            | org.nutz.boot.starter.web3.Web3jStarter|
|157 |web3j.type                              |no        |                    |http      |类型                  | org.nutz.boot.starter.web3.Web3jStarter|
|158 |weixin.aes                              |no        |                    |          |微信公众号API被动消息的AES秘钥  |org.nutz.boot.starter.nutz.weixin.WeixinStarter|
|159 |weixin.appid                            |no        |                    |          |微信公众号的appid         |org.nutz.boot.starter.nutz.weixin.WeixinStarter|
|160 |weixin.appsecret                        |no        |                    |          |微信公众号的appsecret     |org.nutz.boot.starter.nutz.weixin.WeixinStarter|
|161 |weixin.openid                           |no        |                    |          |微信公众号的OpenId        |org.nutz.boot.starter.nutz.weixin.WeixinStarter|
|162 |weixin.token                            |no        |                    |          |微信公众号API被动消息的Token  |org.nutz.boot.starter.nutz.weixin.WeixinStarter|
|163 |wxlogin.appid                           |no        |                    |          |微信登录所需要的appid       |org.nutz.boot.starter.nutz.weixin.WeixinStarter|
|164 |wxlogin.appsecret                       |no        |                    |          |微信登录所需要的appsecret   |org.nutz.boot.starter.nutz.weixin.WeixinStarter|
|165 |wxlogin.host                            |no        |                    |          |微信登录时使用的host头,需要包含http或https|org.nutz.boot.starter.nutz.weixin.WeixinStarter|
|166 |zookeeper.connectionTimeout             |no        |                    |30000     |连接超时,单位毫秒           |org.nutz.boot.starter.zkclient.ZkClientStarter|
|167 |zookeeper.operationRetryTimeout         |no        |                    |-1        |操作超时,单位毫秒           |org.nutz.boot.starter.zkclient.ZkClientStarter|
|168 |zookeeper.sessionTimeout                |no        |                    |30000     |会话超时,单位毫秒           |org.nutz.boot.starter.zkclient.ZkClientStarter|
|169 |zookeeper.zkSerializer                  |no        |                    |org.I0Itec.zkclient.serialize.SerializableSerializer|zookeeper的序列化类      |org.nutz.boot.starter.zkclient.ZkClientStarter|
|170 |zookeeper.zkServers                     |no        |                    |127.0.0.1:2181|服务地址                |org.nutz.boot.starter.zkclient.ZkClientStarter|