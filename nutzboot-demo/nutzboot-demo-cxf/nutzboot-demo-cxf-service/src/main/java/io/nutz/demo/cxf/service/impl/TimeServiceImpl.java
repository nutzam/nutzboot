package io.nutz.demo.cxf.service.impl;

import javax.jws.WebService;

import org.nutz.ioc.loader.annotation.IocBean;

import io.nutz.demo.cxf.service.TimeService;

@WebService(endpointInterface = "io.nutz.demo.cxf.service.TimeService", serviceName = "TimeService")
@IocBean(name = "timeService")
public class TimeServiceImpl implements TimeService {

    public long now() {
        return System.currentTimeMillis();
    }

}
