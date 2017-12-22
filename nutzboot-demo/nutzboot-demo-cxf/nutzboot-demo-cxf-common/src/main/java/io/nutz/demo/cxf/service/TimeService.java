package io.nutz.demo.cxf.service;

import javax.jws.WebService;

@WebService
public interface TimeService {

    long now();
}
