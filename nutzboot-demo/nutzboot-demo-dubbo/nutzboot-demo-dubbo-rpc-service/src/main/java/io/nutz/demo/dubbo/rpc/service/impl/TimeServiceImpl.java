package io.nutz.demo.dubbo.rpc.service.impl;

import org.nutz.ioc.loader.annotation.IocBean;

import com.alibaba.dubbo.config.annotation.Service;

import io.nutz.demo.dubbo.rpc.service.TimeService;

@IocBean
@Service(interfaceClass=TimeService.class)
public class TimeServiceImpl implements TimeService {

	public long now() {
		return System.currentTimeMillis();
	}

}
