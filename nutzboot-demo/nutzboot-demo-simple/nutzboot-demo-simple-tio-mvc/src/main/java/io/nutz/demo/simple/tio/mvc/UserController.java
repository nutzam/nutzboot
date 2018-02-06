package io.nutz.demo.simple.tio.mvc;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.server.annotation.RequestPath;
import org.tio.http.server.util.Resps;

import io.nutz.demo.simple.bean.User;

@IocBean
@RequestPath(value = "/user")
public class UserController {
	
    @Inject
    protected Dao dao;
    
    @RequestPath(value = "/count")
    public HttpResponse count(HttpRequest request) {
        return Resps.json(request, dao.count(User.class));
    }
    
    @RequestPath(value = "/query")
    public HttpResponse query(HttpRequest request) {
        return Resps.json(request, dao.query(User.class, Cnd.orderBy().asc("age"), null));
    }
}
