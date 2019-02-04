package io.nutz.demo.simple.mq;

import org.nutz.integration.zbus.mq.ZbusProducer;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class YvrSuper {

	@Inject("java:$zbus.getProducer('topic_update')")
	protected ZbusProducer topicUpdateMq;

	// 然后 就可以操作zbus的ZBusProducer实例了
	
	public void setTopicUpdateMq(ZbusProducer topicUpdateMq) {
        this.topicUpdateMq = topicUpdateMq;
    }
	
	public ZbusProducer getTopicUpdateMq() {
        return topicUpdateMq;
    }
}