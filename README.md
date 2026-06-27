# 软件服务工程大作业：饿了么外卖平台

本仓库为软件服务工程期末大作业提交仓库，包含 Spring Cloud 后端、Vue 前端、Config Server 远程配置、JMeter 测试资料、运行截图和实验报告。

## 目录结构

```text
elm-springcloud-parent/   Spring Cloud 后端多模块工程
elm-vue/                  Vue 3 + Vite 前端工程
config-repo/              Config Server 读取的远程配置目录
jmeter/                   JMeter 测试计划与结果
screenshots/              运行截图
docs/                     大作业报告
```

## 关键技术

- Eureka 服务注册与发现
- Spring Cloud Gateway 统一网关与路由
- Spring Cloud LoadBalancer 负载均衡
- OpenFeign 远程服务调用
- Gateway CircuitBreaker 熔断降级
- order-service Resilience4j 注解式服务层降级
- Spring Cloud Config + GitHub 配置仓库
- RabbitMQ + Spring Cloud Bus 动态配置刷新
- Vue 3 + Vite 前端页面
- JMeter 接口测试

## 启动顺序

1. 启动 RabbitMQ。
2. 启动 `eureka-server`。
3. 启动 `config-server`。
4. 启动业务服务：`user-service`、`business-service`、`cart-service`、`address-service`、`order-service`。
5. 启动 `gateway-server`。
6. 启动 `elm-vue` 前端。

## Config Server

`config-server` 默认从本仓库的 `config-repo` 目录读取远程配置：

```yaml
spring:
  profiles:
    active: git
  cloud:
    config:
      server:
        git:
          uri: https://github.com/1131824972/software-service-engineering-elm.git
          default-label: main
          search-paths: config-repo
```

动态刷新流程：

1. 修改 `config-repo/business-service.yml`。
2. 向 Config Server 发送 `POST /actuator/busrefresh`。
3. Config Server 通过 RabbitMQ 发布刷新事件。
4. `business-service` 接收刷新事件后更新 `business.notice`。

## 默认账号

```text
账号：10001
密码：123456
```
