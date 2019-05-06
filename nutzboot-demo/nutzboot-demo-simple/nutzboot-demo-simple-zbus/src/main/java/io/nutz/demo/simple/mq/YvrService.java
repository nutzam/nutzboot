package io.nutz.demo.simple.mq;

import org.nutz.integration.zbus.mq.ZbusConsumer;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import io.zbus.mq.Message;
import io.zbus.mq.MqClient;

@IocBean
public class YvrService {
    
    private static final Log log = Logs.get();
    
	@ZbusConsumer(topic="topic_update", verbose=true)
	public void topicUpdate(Message msg, MqClient client) {
		log.info("msg from zbus ---> " + msg);
		msg.setAck(true);
	}
}