package io.nutz.demo.simple;

import org.nutz.boot.NbApp;

public class MainModule {

    public static void main(String[] args) throws Exception {
        new NbApp().setMainClass(MainModule.class).setArgs(args).setAllowCommandLineProperties(true).run();
    }

}
