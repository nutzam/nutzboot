# Jetty配置详解

## http基本配置

|配置名称|默认值|作用|
|--------|------|-------|
|jetty.host|0.0.0.0| 监听的ip, 默认是0.0.0.0, 即全部网卡, 改成127.0.0.1的话,就只能本地访问了|
|jetty.port|8080| 监听的端口|
|jetty.contextPath|| 上下文路径,默认是空字符串|
|jetty.welcome_files|index.html,index.htm,index.do|欢迎页面,逗号分隔就行|
|web.session.timeout|30|回话超时时间,默认30分钟,单位是分钟|


深度配置, 请查阅org.eclipse.jetty.server.HttpConfiguration, 使用配置前缀`jetty.httpConfig.` 进行配置,例如

```
jetty.httpConfig.idleTimeout=600
```

## 开发期相关的配置


|配置名称|默认值|作用|
|--------|------|-------|
|jetty.staticPath|static/,webapp/|静态文件所在的路径|
|jetty.staticPathLocal||静态文件所在的本地路径|

例如, 设置jetty.staticPathLocal=C:/jetty/static 就会优先使用该目录下的文件, 修改后也无需编译,刷新就可

## https配置

虽然jetty支持https,但依然建议使用nginx进行专业的https配置

|配置名称|默认值|作用|
|--------|------|-------|
|jetty.https.port|| 监听的https端口|
|jetty.https.keystore.path|| https证书路径,jks格式|
|jetty.https.keystore.password|| https证书的密钥|
|jetty.https.keymanager.password||https证书管理器的密钥|

深度配置, 请查阅org.eclipse.jetty.server.HttpConfiguration, 使用配置前缀`jetty.httpsConfig.` 进行配置,例如

```
jetty.httpsConfig.idleTimeout=600
```

## 错误页面及错误处理器

示例配置

```
# 配置404页面
jetty.page.404=/error/404.html
# 配置500页面
jetty.page.500=/error/500.html
# 根据抛出的异常配置
page.java.lang.Throwable=/error/any.html
```

## jetty内部细节配置

|配置名称|默认值|作用|
|--------|------|-------|
|jetty.maxFormContentSize|1073741824|最大表单大小,单位是字节,默认是1GB|
|jetty.maxFormKeys|1000| 表单最大key数量|
|jetty.threadpool.idleTimeout|60000|线程池的线程空闲时间,默认60000毫秒|
|jetty.threadpool.minThreads|200|线程池的最小尺寸|
|jetty.threadpool.idleTimeout|600|线程池的最大尺寸|


## gzip压缩传输



|配置名称|默认值|作用|
|--------|------|-------|
|jetty.gzip.enable|false|启用gzip压缩,默认关闭|
|jetty.gzip.level|-1| 压缩级别|
|jetty.gzip.minContentSize|512|最小压缩尺寸|

