# 阿里开源项目 Sentinel: 分布式系统的流量防卫兵 dubbo适配

nutzboot-starter-sentinel-dubbo 

## 控制台启动

* https://github.com/alibaba/Sentinel/releases

```
java -Dserver.port=9090 -Dcsp.sentinel.dashboard.server=localhost:9090 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.4.1.jar
```

## 客户端配置
```
    #是否启用sentinel客户端
    sentinel.enabled=true
    #控制台地址
    sentinel.csp.sentinel.dashboard.server=localhost:9090
    #sentinel客户端端口
    sentinel.csp.sentinel.api.port=8721
    #sentinel客户端通信间隔毫秒数
    sentinel.csp.sentinel.heartbeat.interval.ms=3000
    #sentinel客户端本地IP地址,不设置则自动获取
    sentinel.csp.sentinel.heartbeat.client.ip=
    
    #规则存储的key名
    sentinel.rulekey=nutzboot
    #规则存储的发布订阅频道名
    sentinel.channel=sentinel

    #redis配置内容,用于存储规则,sentinel.enabled=true时初始化加载
    redis.host=127.0.0.1
    redis.port=6379
    redis.timeout=2000
    redis.max_redir=10
    redis.database=0
    redis.maxTotal=100
    redis.pool.maxTotal=100
    redis.pool.maxIdle=50
    redis.pool.minIdle=10
    #redis.password=test123
    #redis集群模式设置 redis.mode=cluster
    redis.mode=normal
    #redis.nodes=
```