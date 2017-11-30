package io.nutz.demo.cxf.service.impl;

import org.nutz.ioc.loader.annotation.IocBean;

import io.nutz.demo.cxf.service.TimeService;
import io.zbus.rpc.Remote;

@Remote
@IocBean
public class TimeServiceImpl implements TimeService {

	public long now() {
		return System.currentTimeMillis();
	}

}
