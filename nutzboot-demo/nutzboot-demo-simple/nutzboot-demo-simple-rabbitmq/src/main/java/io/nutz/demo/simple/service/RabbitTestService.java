package io.nutz.demo.simple.service;

import static org.nutz.integration.rabbitmq.aop.RabbitmqMethodInterceptor.*;

import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class RabbitTestService {

    @Aop("rabbitmq") // 会自动管理Connection/Channel的开启和关闭.
    public void publish(String routingKey, byte[] body) throws Exception {
        channel().basicPublish("", routingKey, null, body);
    }
}