package org.nutz.cloud.loach.server;

import org.nutz.boot.NbApp;

public class LoachLauncher {

    // 访问地址是 http://127.0.0.1:8610
    public static void main(String[] args) {
        new NbApp().setArgs(args).setPrintProcDoc(true).run();
    }

}
