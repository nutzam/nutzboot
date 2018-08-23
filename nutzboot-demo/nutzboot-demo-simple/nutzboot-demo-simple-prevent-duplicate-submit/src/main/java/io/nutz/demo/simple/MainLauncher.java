package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.boot.starter.prevent.duplicate.submit.annotation.Token;
import org.nutz.boot.starter.prevent.duplicate.submit.annotation.Type;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.ChainBy;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;

@IocBean(create = "init")
@ChainBy(args = "chain/nutzfw-mvc-chain.js")
public class MainLauncher {

    private static final Log log = Logs.get();

    @At({"/index"})
    @Ok("beetl:/index.html")
    @Token
    public NutMap index() {
        NutMap obj = new NutMap();
        obj.setv("name", "index_1");
        return obj;
    }

    @POST
    @At("/index")
    @Ok("json")
    @Token(type = Type.REMOVE)
    public NutMap postIndex() {
        return new NutMap().setv("msg", "访问到数据啦，尝试刷新下页面看看？");
    }


    @At({"/index2"})
    @Ok("beetl:/index.html")
    @Token(path = "test")
    public NutMap index2() {
        i = 0;
        NutMap obj = new NutMap();
        obj.setv("name", "index_2");
        return obj;
    }


    int i = 0;

    @POST
    @At("/index2")
    @Ok("json")
    @Token(type = Type.REMOVE, path = "test")
    public NutMap postIndex2() {
        i++;
        if (i == 1) {
            throw new RuntimeException("手动触发异常！请刷新页面试试！");
        } else {
            return new NutMap().setv("msg", "访问到数据啦！");
        }
    }


    @Inject("refer:$ioc")
    protected Ioc ioc;

    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

    public void init() {
        System.out.println("请访问： http://127.0.0.1:8080/index");
        System.out.println("请访问错误后恢复token： http://127.0.0.1:8080/index2");
    }

}
