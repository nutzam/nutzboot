package org.nutz.boot.tools;

import java.lang.management.ManagementFactory;

public class NbTools {

    /**
     * 获取进程id
     * @param fallback 如果获取失败,返回什么呢?
     * @return 进程id
     */
    public static String getProcessId(final String fallback) {
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf('@');
        if (index < 1) {
            return fallback;
        }
        try {
            return Long.toString(Long.parseLong(jvmName.substring(0, index)));
        }
        catch (NumberFormatException e) {
        }
        return fallback;
    }
}
