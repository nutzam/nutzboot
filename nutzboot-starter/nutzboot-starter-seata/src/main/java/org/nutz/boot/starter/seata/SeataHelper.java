package org.nutz.boot.starter.seata;

import io.seata.config.ConfigurationFactory;

public class SeataHelper {

    public static boolean disableGlobalTransaction = ConfigurationFactory.getInstance().getBoolean("service.disableGlobalTransaction", false);
}
