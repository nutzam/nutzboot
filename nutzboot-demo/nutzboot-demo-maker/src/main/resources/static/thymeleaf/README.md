# 简介

本压缩包是一个maven工程, eclipse/idea均可按maven项目导入

MainLauncher是入口,启动即可

## 环境要求

* 必须JDK8+
* eclipse或idea等IDE开发工具,可选

## 配置信息位置

数据库配置信息,jetty端口等配置信息,均位于src/main/resources/application.properties

## 命令下启动

仅供测试用,使用mvn命令即可

```
// for windows
set MAVEN_OPTS="-Dfile.encoding=UTF-8"
mvn compile exec:java -Dexec.mainClass="io.nutz.demo.MainLauncher"

// for *uix
export MAVEN_OPTS="-Dfile.encoding=UTF-8"
mvn compile exec:java -Dexec.mainClass="io.nutz.demo.MainLauncher"
```

## 相关资源
论坛: https://nutz.cn
官网: https://nutz.io
一键生成NB的项目: https://get.nutz.io