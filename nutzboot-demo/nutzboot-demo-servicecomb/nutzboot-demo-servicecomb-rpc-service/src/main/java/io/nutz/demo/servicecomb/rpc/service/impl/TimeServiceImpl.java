package io.nutz.demo.servicecomb.rpc.service.impl;

import org.apache.servicecomb.provider.pojo.RpcSchema;
import org.nutz.ioc.loader.annotation.IocBean;

import io.nutz.demo.servicecomb.rpc.service.TimeService;

@IocBean
@RpcSchema(schemaId="time")
public class TimeServiceImpl implements TimeService {

	public long now() {
		return System.currentTimeMillis();
	}

}
