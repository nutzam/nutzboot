# nutzboot

简称NB,基于Nutz的微服务方案

* 主页: [NB的官网](https://nutz.io)
* 项目生成器: [NB Makder](https://get.nutz.io)
* 版本历史: [NB进化史](ChangeLog.md)
* 文档: [NB的文档](http://nutzam.com/core/boot/overview.html)

## 来个NB的demo

```java
package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.*;
import org.nutz.mvc.annotation.*;

@IocBean
public class MainLauncher {
    
    @Ok("raw")
    @At("/time/now")
    public long now() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) throws Exception {
        new NbApp(MainLauncher.class).run();
    }
}
```

### 开发计划

期待您的加入

- [ ] 基础框架
	- [ ] 基础框架的文档
	- [x] 基本框架的实现
- [ ] 服务器类启动器
	- [ ] web类启动器
		- [x] [Jetty](https://www.eclipse.org/jetty/)
		- [ ] [Tomcat](http://tomcat.apache.org/)
		- [ ] [Undertow](http://undertow.io/)
	- [ ] 非Web类启动器
		- [ ] [netty](https://netty.io/)
		- [ ] [mina](https://mina.apache.org/)
		- [ ] [t-io](http://www.oschina.net/p/t-io)
		- [ ] mqtt
		- [ ] pure tcp/udp
	- [ ] Rpc类启动器
		- [x] [Dubbo](http://dubbo.io/)
		- [x] [zbus](http://zbus.io)
		- [ ] [motan](https://github.com/weibocom/motan)
- [ ] 数据库类相关
	- [ ] 关系型数据库
		- [x] Jdbc连接池
		- [x] Nutz.Dao
		- [ ] BeetlSql
		- [ ] Hibernate
		- [ ] mybatis
	- [ ] 非关系型数据库
		- [x] [Redis](https://redis.io)
		- [ ] MongoDB
		- [ ] neo4j
		- [ ] memcached
		- [ ] ssdb
		- [ ] Cassandra
		- [ ] HBase
		- [ ] rethinkdb
- [ ] Mvc
	- [x] Nutz.Mvc
- [ ] 安全鉴权
	- [x] [Shiro](http://shiro.apache.org)
- [ ] 计划任务
	- [x] [Quartz](http://www.quartz-scheduler.org)
- [ ] 模板引擎
	- [x] [Beetl](http://ibeetl.com/)
	- [ ] [jetbrick-template](https://github.com/subchen/jetbrick-template-2x)
	- [ ] Vecloity
	- [ ] FreeMarker
	- [ ] Thymeleaf
- [ ] 消息队列
	- [x] disque
	- [ ] zeromq
	- [ ] rabbitmq
	- [ ] rocketmq
	- [ ] activemq
- [ ] 工作流
	- [ ] [uflo2](https://github.com/youseries/uflo)
	- [ ] Activity
- [ ] 规则引擎
	- [ ] urule
	- [ ] drools
- [ ] 开放平台
	- [x] 微信公众号开放平台
- [ ] 云平台
	- [ ] [阿里云](https://aliyun.com)
	- [ ] [腾讯云](https://qcloud.com)
- [ ] 缓存相关
	- [x] wkcache 方法缓存
- [ ] docker相关
	- [ ] docker compose配置

## 第三方starter或项目

期待您的到来,报个issue告知一下吧 ^_^

## 授权协议

与Nutz一样, NutzBoot遵循[Apache协议](LICENSE),完全开源,文档齐全,永远免费(商用也是)