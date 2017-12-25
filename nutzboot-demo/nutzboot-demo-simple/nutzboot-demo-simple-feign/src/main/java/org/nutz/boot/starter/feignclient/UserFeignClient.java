package org.nutz.boot.starter.feignclient;

import feign.Param;
import feign.RequestLine;
import org.nutz.boot.starter.feign.annotation.FeignClient;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;
import java.util.Map;

/**
 *
 */
@IocBean
@FeignClient
public interface UserFeignClient {

    @RequestLine("GET /")
    Map index();

    @RequestLine("POST /user/list")
    List<Map>  list(@Param("id") String id);
}
