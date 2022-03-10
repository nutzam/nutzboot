# NutzBoot 可靠的企业级微服务框架


```
 _   _ ______                                      ___   
| \ | || ___ \  ______ ______ ______ ______ ______| \ \  
|  \| || |_/ / |______|______|______|______|______| |\ \ 
| . ` || ___ \  ______ ______ ______ ______ ______| | > >
| |\  || |_/ / |______|______|______|______|______| |/ / 
\_| \_/\____/                                     |_/_/  
  
:: Nutz Boot ::
```

* 主页: [NB的官网](https://nutz.io)
* 关于Nutz: [Nutz](https://github.com/nutzam/nutz)
* 项目生成器: [NB Maker](https://get.nutz.io)
* 推荐项目: [NutzWk](https://github.com/Wizzercn/NutzWk)
* 推荐项目: [NutzSite](https://github.com/HaimmingYu/NutzSite)
* 版本历史: [NB进化史](ChangeLog.md)
* 文档: [NB的文档](https://gitee.com/nutz/nutzboot/tree/dev/doc)
* 社区: [NutzCN](https://nutz.cn)
* Idea插件 [NutzCodeInsight](https://github.com/threefish/NutzCodeInsight) 开发利器
* QQ群: 68428921(已满) 24457628(2群) 58444676(老吹水群)

[![Build Status](https://travis-ci.org/nutzam/nutzboot.png?branch=dev)](https://travis-ci.org/nutzam/nutzboot)
[![CircleCI](https://circleci.com/gh/nutzam/nutzboot/tree/dev.svg?style=svg)](https://circleci.com/gh/nutzam/nutzboot/tree/dev)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutzboot-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutzboot-parent/)
[![GitHub release](https://img.shields.io/github/release/nutzam/nutzboot.svg)](https://github.com/nutzam/nutzboot/releases)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Skywalking Tracing](https://img.shields.io/badge/Skywalking%20Tracing-enable-brightgreen.svg)](https://github.com/OpenSkywalking/skywalking)

## NB功能介绍

* 快速创建Nutz应用,[初始化工具Maker](https://get.nutz.io)
* 嵌入式web服务(jetty/tomcat/undertow),可打包成单一jar文件
* 基于starter的自动配置体系,只需要添加maven依赖,即可自动发现并加载
* 能满足90%以上常见需求的默认配置,无需过多的自定义
* 以开放的心态与国内开源团体合作,优先集成国产项目
* 活跃的社区及稳健的发布周期,推进项目一直前进
* 提供swagger api文件自动生成

## NB贡献者(Contributors)

* [蛋蛋](https://github.com/Eggsblue)(提交了第一个Banner及打印逻辑)及starter-tio和starter-j2cache
* [胖五](https://github.com/pangwu86)(nutz.io主笔)
* [qinerg](https://github.com/qinerg)(率先提交undertow)
* [benjobs](https://github.com/wolfboys)(提交了tomcat)
* [温泉](https://github.com/ywjno)(提交thymeleaf和eureka静态status页面)
* [科技](https://github.com/Rekoe)(探路者,正在踩坑,正在做后台模板)
* [潇潇](https://github.com/howe)(探路者,生产环境填坑中)
* [道坤](https://github.com/albinhdk)(探路者,提交ssdb)
* [HeTaro](https://gitee.com/HeTaro)(探路者,正在踩坑)
* [zozoh](https://github.com/zozoh)(路过...)
* [wendal](https://github.com)(到处挖坑)
* [瞎折腾](https://gitee.com/lx19990999)(完善demo-maker)
* [天空](https://github.com/tiankongkm)(提交zkclient)
* [haoqoo](https://github.com/haoqoo)(提交velocity)
* [鱼夫](https://gitee.com/yustory)(正在踩NB+U家三剑客的坑)
* [幸福的旁边](https://github.com/happyday517)(提交caffeine方法缓存)
* [文涛](https://gitee.com/wentao0291) (新增支持加载外部配置文件，新增多数据库连接支持)
* [zjSniper](https://gitee.com/zjSniper) (优化starter-tio的逻辑)
* [tasdingoo](https://github.com/tasdingoo)(issue@github 122)
* [csl_slchia](https://gitee.com/csl_slchia)(issue@gitee II92L)
* [大鲨鱼](https://github.com/Wizzercn)(提交starter-wkcache/elasticsearch/sentinel/swagger3等,扩展NB功能)
* [threefish](https://github.com/threefish)(Nutz Intellij idea插件,提交starter-email/sqlXmlTpl)
* 还有您的名字哦,告知我们吧

## NB资源

* [NB组件](doc2/Components.md)
* [NB示例](doc2/Demos.md)
* [NB快速开始](doc2/QuickStart.md)
* [NB公共服务](doc2/CommonService.md)
* [采用公司](https://github.com/nutzam/nutzboot/issues/62)

## NB文档

* [NutzBoot简介](doc/overview.md)
* [NutzBoot目录约定](doc/struct.md)
* [NB与Nutz.Mvc对比](doc/diff_nb_mvc.md)
* [转换为NB项目](doc/convert2nb.md)
* [Maven Plugin](https://github.com/nutzam/nutzboot-maven-plugin)
* [配置信息总表](doc/configure.md) 不定期更新,可通过nutzboot:propdoc生成
* [Jetty配置详解](doc/jetty_usage.md)
* [添加Web过滤器](doc/add_web_filter.md)

## 授权协议

与Nutz一样, NutzBoot遵循[Apache协议](LICENSE),完全开源,文档齐全,永远免费(商用也是)

