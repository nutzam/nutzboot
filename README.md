# nutzboot
简称NB

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

- [ ] 基础框架
	- [ ] 基础框架的文档
	- [x] 基本框架的实现
- [ ] 服务器类启动器
	- [ ] web类启动器
		- [x] Jetty
		- [ ] Tomcat
		- [ ] Undertow
	- [ ] 非Web类启动器
		- [ ] netty
		- [ ] mima
		- [ ] mqtt
		- [ ] pure tcp/udp
		- [ ] t-io
	- [ ] Rpc类启动器
		- [x] Dubbo
		- [x] zbus
- [ ] 数据库类相关
	- [ ] 关系型数据库
		- [x] Jdbc连接池
		- [x] Nutz.Dao
	- [ ] 非关系型数据库
		- [x] Redis
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
	- [x] Shiro
- [ ] 计划任务
	- [x] Quartz
- [ ] 模板引擎
	- [ ] Beetl
	- [ ] Vecloity
	- [ ] FreeMarker
	- [ ] Thymeleaf
- [ ] 消息队列
	- [ ] zeromq
	- [ ] rabbitmq
	- [ ] rocketmq
	- [ ] activemq
- [ ] 工作流
	- [ ] uflo2
	- [ ] Activity
- [ ] 规则引擎
	- [ ] urule
	- [ ] drools
- [ ] 开放平台
	- [x] 微信公众号开放平台
- [ ] 云平台
	- [ ] 阿里云
	- [ ] 腾讯云
- [ ] 缓存相关
	- [x] wkcache 方法缓存
- [ ] docker相关
	- [ ] docker compose配置