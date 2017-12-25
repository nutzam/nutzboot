# NutzBoot 微服务

NutzBoot,简称NB,是可靠的企业级微服务框架,提供自动配置,嵌入式web服务,分布式会话,RPC等一篮子解决方案

* 主页: [NB的官网](https://nutz.io)
* 项目生成器: [NB Makder](https://get.nutz.io)
* 版本历史: [NB进化史](ChangeLog.md)
* 文档: [NB的文档](http://nutzam.com/core/boot/overview.html)

[![Build Status](https://travis-ci.org/nutzam/nutzboot.png?branch=dev)](https://travis-ci.org/nutzam/nutzboot)
[![GitHub release](https://img.shields.io/github/release/nutzam/nutzboot.svg)](https://github.com/nutzam/nutzboot/releases)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Skywalking Tracing](https://img.shields.io/badge/Skywalking%20Tracing-enable-brightgreen.svg)](https://github.com/OpenSkywalking/skywalking)

## 功能介绍

* 快速创建Nutz应用,提供[初始化工具Maker](https://get.nutz.io)
* 嵌入式web服务(jetty/tomcat/undertow),直接打包为runnable jar
* 基于starter的自动配置体系,只需要添加maven依赖,即可自动发现并加载
* 能满足80%以上常见需求的默认配置,无需过多的自定义
* 以开放的心态与国内开源团体合作,优先集成国产项目
* 活跃的社区及稳健的发布周期,推进项目一直前进

## 快速预览一下NB的项目吧

pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>org.nutz</groupId>
        <artifactId>nutzboot-starter</artifactId>
        <version>${nutzboot.version}</version>
    </dependency>
    <dependency>
        <groupId>org.nutz</groupId>
        <artifactId>nutzboot-starter-nutz-web</artifactId>
        <version>${nutzboot.version}</version>
    </dependency>
    <dependency>
        <groupId>org.nutz</groupId>
        <artifactId>nutzboot-starter-jetty</artifactId>
        <version>${nutzboot.version}</version>
    </dependency>
</dependencies>
```

src/main/java/io/nutz/demo/simple/MainLauncher.java

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
        new NbApp().run();
    }
}
```

[![asciicast](https://asciinema.org/a/40Brr8ZNsHx1ILfjhJ7zppJ3v.png)](https://asciinema.org/a/40Brr8ZNsHx1ILfjhJ7zppJ3v)

请访问 [https://get.nutz.io](https://get.nutz.io) 获取属于您的基础代码



### Demo

* [内置demo](https://github.com/nutzam/nutzboot/tree/dev/nutzboot-demo),每个starter均配套一个demo
* [Todo-backend](https://github.com/nutzam/todo-backend-nutzboot),一个类就能通过TodoBackend的测试


## Contributors

* [蛋蛋](https://github.com/TopCoderMyDream)(提交了第一个Banner及打印逻辑)
* [胖五](https://github.com/pangwu86)(nutz.io主笔)
* [qinerg](https://github.com/qinerg)(率先提交undertow)
* [benjobs](https://github.com/wolfboys)(提交了tomcat)
* [温泉](https://github.com/ywjno)(提交thymeleaf)
* [科技](https://github.com/Rekoe)(探路者,正在踩坑,正在做后台模板)
* [潇潇](https://github.com/howe)(探路者,生产环境填坑中)
* [道坤](https://github.com/albinhdk)(探路者,正在踩坑)
* [HeTaro](https://gitee.com/HeTaro)(探路者,正在踩坑)
* [zozoh](https://github.com/zozoh)(路过...)
* [wendal](https://github.com)(到处挖坑)
* [瞎折腾](https://gitee.com/lx19990999)(添加了demo-maker的几个选项)
* [天空](https://github.com/tiankongkm)(提交zkclient)
* [haoqoo](https://github.com/haoqoo)(提交velocity)
* 还有您的名字哦,告知我们吧

## 采用NutzBoot的公司

请访问链接 [采用公司](https://github.com/nutzam/nutzboot/issues/62)

## 文档

* [NutzBoot简介](doc/overview.md)
* [NutzBoot目录约定](doc/struct.md)
* [NB与Nutz.Mvc对比](doc/diff_nb_mvc.md)

## 开发进度

期待您的加入

- 基础框架
	- [x] 基础框架的文档
	- [x] 基本框架的实现
- 服务器类启动器
	- web类启动器
		- [x] [Jetty](https://www.eclipse.org/jetty/)
		- [x] [Undertow](http://undertow.io/) by [@qinerg](https://github.com/qinerg)
		- [x] [Tomcat](http://tomcat.apache.org/) by [@benjobs](https://github.com/wolfboys)
	- 非Web类启动器
		- [ ] [netty](https://netty.io/)
		- [ ] [mina](https://mina.apache.org/)
		- [ ] [t-io](http://www.oschina.net/p/t-io)
	- Rpc类启动器
		- [x] [Dubbo](http://dubbo.io/) 阿里出品的高性能RPC平台
		- [x] [zbus](http://zbus.io) 国产知名RPC平台
		- [ ] [motan](https://github.com/weibocom/motan)
	- 其他
		- [x] Ngrok 内网穿透,轻松获取外网地址
- 数据库类相关
	- 关系型数据库
		- [x] Jdbc连接池,默认使用druid,带监控功能
		- [x] [sharding-jdbc](https://github.com/shardingjdbc/sharding-jdbc) 分库分表
		- [x] [Nutz.Dao](https://github.com/nutzam/nutz)
		- [x] [BeetlSql](http://ibeetl.com/guide/#beetlsql) 基于Beetl的SQL框架
		- [ ] Hibernate
		- [ ] mybatis
	- 非关系型数据库
		- [x] [Redis](https://redis.io)
		- [x] MongoDB
		- [ ] neo4j
		- [ ] memcached
		- [ ] ssdb
		- [ ] Cassandra
		- [ ] HBase
		- [ ] rethinkdb
- Mvc
	- [x] Nutz.Mvc
	- [ ] [jersey](https://jersey.github.io/)
- 安全鉴权
	- [x] [Shiro](http://shiro.apache.org)
- 分布式Session
	- [x] [Shiro+LCache](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-cache)基于shiro/jedis/插件的分布式可持久化的session缓存
- 计划任务
	- [x] [Quartz](http://www.quartz-scheduler.org)
- 模板引擎
	- [x] [Beetl](http://ibeetl.com/) 
	- [x] [jetbrick-template](https://github.com/subchen/jetbrick-template-2x)
	- [x] Velocity by [haoqoo](https://github.com/haoqoo)
	- [ ] FreeMarker
	- [x] Thymeleaf by [温泉](https://github.com/ywjno)
- 消息队列
	- [x] disque redis作者的另一作品
	- [ ] zeromq
	- [ ] rabbitmq
	- [ ] rocketmq
	- [ ] activemq
	- [ ] zbus
- 工作流
	- [x] [uflo2](https://github.com/youseries/uflo) 中式工作流引擎
	- [ ] Activity
- 规则引擎
	- [x] [urule](https://github.com/youseries/urule) 中式规则引擎
	- [ ] drools
- 报表系统
	- [x] ureport 中式报表
	- [ ] jreport
- 开放平台
	- 微信公众号开放平台
		- [x] [NutzWX](https://github.com/nutzam/nutzwx) Weixin Api By Nutz
		- [ ] [weixin-java-tools](https://gitee.com/binary/weixin-java-tools)
- 云平台
	- [ ] [阿里云](https://aliyun.com)
	- [ ] [腾讯云](https://qcloud.com)
- 缓存相关
	- [x] wkcache 方法缓存 by 大鲨鱼
- docker相关
	- [ ] docker compose配置
- WebService
	- [x] CXF
- 分布式组件
	- [x] zkclient zookeeper的封装
## 第三方starter或项目

期待您的到来,报个issue告知一下吧 ^_^

## 授权协议

与Nutz一样, NutzBoot遵循[Apache协议](LICENSE),完全开源,文档齐全,永远免费(商用也是)
