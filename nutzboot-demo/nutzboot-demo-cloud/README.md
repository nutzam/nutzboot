# Cloud Demo

演示 服务治理(eureka),负载均衡(ribbon-loadbalancer),远程调用(feign),熔断器(hystrix) 一起工作

## 使用说明

* 先启动 nutzboot-demo-cloud-eureka-server, 将占有 8080端口
* 访问 http://127.0.0.1:8080/eureka/status 等待30以上,刷新页面
* 修改nutzboot-demo-cloud-service下的数据库连接信息
* 启动 nutzboot-demo-cloud-service, 将占有8083端口, 它会注册一个服务,叫 "feign-service"
* 等待30秒左右,刷新eureka的status.jsp,可以看到"feign-service"注册信息
* 启动 nutzboot-demo-cloud-client,它占有8082端口
* 打开浏览器,访问 http://127.0.0.1:8082/user/apitest
