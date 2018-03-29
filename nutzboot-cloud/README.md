# NutzCloud

## 模块划分

- [x] gateway-server API网关
- [x] loach-server 服务注册与服务发现的服务端
- [x] loach-client 服务注册与服务发现的客户端
- [x] literpc 简洁高效的RPC服务
- [x] cloud-demo 演示组件间的协作

## 编译说明

### 独立使用的组件

对应`mvn -Pstandalone`
- gateway-server
- loach-server

他们均自带Launcher, 可直接启动

### demo

对应`mvn -Pdemo`