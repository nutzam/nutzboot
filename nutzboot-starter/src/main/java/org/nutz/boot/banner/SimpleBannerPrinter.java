package org.nutz.boot.banner;

import java.io.IOException;
import java.io.InputStream;

import org.nutz.boot.AppContext;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.log.Logs;

/**
 * NB启动时的LOGO加载类
 *
 * @Author 蛋蛋-wqh
 * @author wendal(wendal198@gmail.com)
 * @Date 2017年11月7日 19:04:42
 */

public class SimpleBannerPrinter {

    public void printBanner(AppContext ctx) {
        try {
            InputStream ins = ctx.getResourceLoader().get("banner.txt");
            if (ins == null) {
                ins = ctx.getResourceLoader().get("_banner.txt");
            }
            if (ins == null) {
                return;
            }
            Logs.get().debug("\r\n"+Lang.readAll(Streams.utf8r(ins)));
        }
        catch (IOException e) {
            // nop
        }
    }

}
