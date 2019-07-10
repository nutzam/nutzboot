package org.nutz.cloud.loach.server.util;

import org.nutz.lang.util.NutMap;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wizzer on 2018/4/6.
 */
public class SystemStatusUtil {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss Z";

    public static NutMap getSystemStatus() {
        NutMap result = NutMap.NEW();
        result.put("server-curtime", getCurrentTimeAsString());
        result.put("server-uptime", getUpTime());
        Runtime runtime = Runtime.getRuntime();
        int totalMem = (int) (runtime.totalMemory() / 1048576);
        int freeMem = (int) (runtime.freeMemory() / 1048576);
        int usedPercent = (int) (((float) totalMem - freeMem) / (totalMem) * 100.0);
        result.put("num-of-cpus",
                String.valueOf(runtime.availableProcessors()));
        result.put("total-avail-memory",
                String.valueOf(totalMem) + "mb");
        result.put("current-memory-usage",
                String.valueOf(totalMem - freeMem) + "mb" + " ("
                        + usedPercent + "%)");

        return result;
    }

    public static String getUpTime() {
        long diff = ManagementFactory.getRuntimeMXBean().getUptime();
        diff /= 1000 * 60;
        long minutes = diff % 60;
        diff /= 60;
        long hours = diff % 24;
        diff /= 24;
        long days = diff;
        StringBuilder buf = new StringBuilder();
        if (days == 1) {
            buf.append("1 day ");
        } else if (days > 1) {
            buf.append(Long.valueOf(days).toString()).append(" days ");
        }
        DecimalFormat format = new DecimalFormat();
        format.setMinimumIntegerDigits(2);
        buf.append(format.format(hours)).append(":")
                .append(format.format(minutes));
        return buf.toString();
    }

    public static String getCurrentTimeAsString() {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        return format.format(new Date());
    }
}
