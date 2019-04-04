package org.nutz.boot.starter.fescar;

import com.alibaba.fescar.config.ConfigurationFactory;

public class FescarHelper {

    public static boolean disableGlobalTransaction = ConfigurationFactory.getInstance().getBoolean("service.disableGlobalTransaction", false);
}
