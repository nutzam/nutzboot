package org.nutz.boot.resource;

import org.nutz.Nutz;
import org.nutz.boot.resource.impl.SimpleResourceLoader;
import org.nutz.log.Logs;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * NB启动时的LOGO加载类
 *
 * @Author 蛋蛋-wqh
 * @Date 2017年11月7日 19:04:42
 */

public class StaredLogoLoaderDD {

    public static void printLogo(){
        try {
            String str = "\n \n  _   _          _        \n" +
            " | \\ | | _   _ _| |_  ____\n" +
                    " |  \\| || | | |_| __||_  /\n" +
                    " | |\\  || |_| | | |_  / / \n" +
                    " |_| \\_| \\__,_|  \\__|/___|" +
                    "\n:: Nutz Boot ::   ("+ Nutz.version()+")\n";
            InputStream inputStream = new SimpleResourceLoader().get("banner.txt");
            if(inputStream!=null){
                str = "";
                try (BufferedInputStream bi = new BufferedInputStream(inputStream)) {
                    byte[] buf = new byte[1024];
                    int read = 0;
                    while((read = bi.read(buf))!=-1){
                        str+=new String(buf,"utf-8");
                    }
                    str+="\n:: Nutz Boot ::   ("+ Nutz.version()+")\n";
                }
            }
            Logs.get().debug(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
