package io.nutz.demo.custom;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;

import io.nutz.demo.custom.starter2.MyStarter2Add;

@IocBean
public class MainLauncher {

    public static void main(String[] args) {
        NbApp app = new NbApp();
        // 这里演示2种starter加载方式
        // 第一种,io.nutz.demo.custom.starter.MySimpleServerStarter
        //   它声明在 resources/META-INF/nutz/org.nutz.boot.starter.NbStarter
        //   不需要在代码中指明
        // 第二种, 自行添加, io.nutz.demo.custom.starter2.MyStarter2Add
        app.addStarterClass(MyStarter2Add.class);
        app.setPrintProcDoc(true);
        app.run();
    }
}
