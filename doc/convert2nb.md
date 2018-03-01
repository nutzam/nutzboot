# 将普通JavaEE项目转变为NutzBoot的项目

## 准备事项/注意事项

* 请先浏览一下其他文档,对NB有起码的了解,尤其是项目结构
* 原项目需要是maven项目, 否则需要先转为maven项目的结构
* NutzBoot默认不带jsp支持, 若需要jsp支持, 容器必须选jetty

## 第1步,从get.nutz.io下载项目模板

地址: https://get.nutz.io
需要选中的项目: Nutz.Mvc,Jetty

其他starter暂时不要选,在改造过程中逐渐添加!!!

将下载得到的压缩包解压到一个目录下,得到

```
- src
	- main
		- java
		    ....
pom.xml	
```

模板项目提供了标准化的pom.xml和基础MainLauncher类,可以少掉坑

## 第2步,修改pom.xml

### 将原项目中的dependencies拷贝过来

除了javax.servlet-api, 或者servlet-api, 因为nutzboot默认已经提供,所以不需要添加,否则无法启动

## 第3步,拷贝源文件

* 将原项目中的src目录直接拷贝过来
* 把src/main/webapp 移动到 src/main/resources/webapp/

## 第4步,将项目导入到eclipse/idea

确保编译能通过就行,暂时还不能启动

## 第5步,移动MainLauncher

把MainLauncher类, 移动到MainModule所在的package, 并修改main方法的内容为

```java
new NbApp(MainModule.class).setArgs(args).setPrintProcDoc(true).run();
```

并修改pom.xml里面MainLauncher的package

**注意** 确保MainModule在顶层package, 例如

```
// 错误示例
net.wendal.pdf2xxx.main.MainModule
net.wendal.pdf2xxx.module.MainModule
// 正确位置
net.wendal.pdfxx.MainModule
```

如果由于某些特殊的原因,不能移动MainModule,那么修改main方法为

```java
NbApp nb = new NbApp(MainModule.class).setArgs(args).setPrintProcDoc(true);
nb.getAppContext().setMainPackage("net.wendal.pdfxx");
nb.run();
```


## 第6步, 修改web.xml

将web.xml中关于NutFilter的声明移除

## 第7步,修改MainModule类

通常情况下, 其@IocBy是这样子的, 请务必先格式化成下面的样子,一行一个参数

```java
@IocBy(args = {
               "*js",
               "ioc/",
               "*anno",
               "net.wendal.nutzbook.web",
               "*quartz", // 关联Quartz
               "*async", "128",
               "*tx",
               "*jedis",
               "*slog"
               })
```

修改为(留意需要注释的行)

```java
@IocBy(args = {
               // 默认js加载路径就是ioc,所以去掉
               //"*js",
               //"ioc/",
               // 默认anno就是MainModule所在的package,所以去掉
               //"*anno",
               //"net.wendal.nutzbook.web",
               "*quartz", // 关联Quartz
               // 已默认配置, 线程池大小可以通过配置文件修改
               //"*async", "128",
               // 已默认配置
               //"*tx",
               "*jedis",
               "*slog"
               })
```

提醒: 例如quartz和jedis,添加相应的starter后, iocby里面的行就可以注释掉了

## 检查ioc配置文件

* 通常来说,原项目会有dao.js或dao.json或者db.js, 里面有conf对象,我们需要将它的定义删除
* 如果项目的ioc js文件不在`src/main/resources/ioc/`, 那么将它们移动过去

## 尝试启动

右键MainLauncher, 启动之

## 可能出现的问题

### 报nutz的某个类的某个方法不存在

Q: NoSuchMethod .... setMainModule
A: 原项目引用了老版本的nutz, 在pom.xml中添加下面配置

```xml
		<dependency>
			<groupId>org.nutz</groupId>
			<artifactId>nutz</artifactId>
		</dependency>
```

## 如何打包

命令行下

```
mvn clean package
```

在target目录下会生成一个

## 后续操作

* 添加nutzboot-starter-nutz-dao依赖后, 删除dao.js/dao.json
* 添加nutzboot-starter-redis依赖后,删除@IocBy中的`*jedis`
* 添加nutzboot-starter-quartz依赖后, 删除@IocBy中的`*quartz`,且MainSetup中的初始化quartz代码可以删除

请查阅nutzboot-demo目录下的各种demo