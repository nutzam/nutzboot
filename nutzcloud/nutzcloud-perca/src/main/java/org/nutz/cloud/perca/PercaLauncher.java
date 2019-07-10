package org.nutz.cloud.perca;

import org.nutz.boot.NbApp;

public class PercaLauncher {

    public static void main(String[] args) {
        new NbApp().setArgs(args).setPrintProcDoc(true).run();
    }

}
