package io.nutz.demo.simple;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

public class MainSetup implements Setup {

    private static final Log log = Logs.get();
    
    public void init(NutConfig nc) {
        log.info("Hello, So NB!");
    }

    public void destroy(NutConfig nc) {
    }

    
}
