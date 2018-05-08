# NutzBoot与NutzMvc的关系

用表格来说说吧

|  功能点  |NutzBoot|Nutz.MVC|
|---------|---------|-------|
|运行环境   | 独立运行 |Web容器(Jetty/Tomcat等)|
|打包方式   | Jar/war     | war   |
|灵活性       | 一般       | 高       |
|支持分布式| 容易        | 一般   |
|自动reload|尚无         |依赖容器|
|动静分离    | 方便       | 不太友好|
|配置文件   | 有           | 有        |
|命令行参数|有             |没有      |
|环境参数    |有            |有          |
|主入口       |MainLauncher|MainModule|

补充说明:
* 通过[Maven Plugin](https://github.com/nutzam/nutzboot-maven-plugin)可以把nutzboot项目打包成war
* 通过nutzboot-servlet3,可以直接把nutzboot配置到web.xml中加载
* MainLauncher能使用MainModule的大部分注解(除了@LoadingBy注解),而且只是多了个main方法
* 在NutzBoot中,Nutz.MVC会作为一个starter存在,99%以上的功能都能如常使用
* NutzBoot灵活性低一些是因为规范了最佳实践,降低了随意性
* 自动reload指类文件的reload,页面文件不受影响