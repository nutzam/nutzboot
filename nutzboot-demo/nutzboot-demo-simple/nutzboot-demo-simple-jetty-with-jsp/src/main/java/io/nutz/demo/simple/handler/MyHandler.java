package io.nutz.demo.simple.handler;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@IocBean
public class MyHandler extends ErrorPageErrorHandler {
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(response.getStatus() == 404){
            response.getWriter().write(Json.toJson(new NutMap("code","-1").setv("msg","404 error")));
            return;
        }
        super.handle(target, baseRequest, request, response);
    }
}
