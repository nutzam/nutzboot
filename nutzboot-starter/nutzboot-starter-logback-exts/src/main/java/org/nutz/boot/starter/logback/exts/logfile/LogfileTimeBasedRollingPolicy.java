package org.nutz.boot.starter.logback.exts.logfile;

import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import org.nutz.lang.Lang;

public class LogfileTimeBasedRollingPolicy<E> extends TimeBasedRollingPolicy<E> {
    @Override
    public void start() {
        String fileNamePattern = super.getFileNamePattern();
        if (fileNamePattern.toLowerCase().endsWith(".log")) {
            fileNamePattern = fileNamePattern.substring(0, fileNamePattern.length() - 4) + "-" + Lang.JdkTool.getProcessId("0") + ".log";
        } else if (fileNamePattern.toLowerCase().endsWith(".gz")) {
            fileNamePattern = fileNamePattern.substring(0, fileNamePattern.length() - 3) + "-" + Lang.JdkTool.getProcessId("0") + ".gz";
        } else if (fileNamePattern.toLowerCase().endsWith(".zip")) {
            fileNamePattern = fileNamePattern.substring(0, fileNamePattern.length() - 4) + "-" + Lang.JdkTool.getProcessId("0") + ".zip";
        } else {
            fileNamePattern = fileNamePattern + "-" + Lang.JdkTool.getProcessId("0") + ".log";
        }
        super.setFileNamePattern(fileNamePattern);
        super.start();
    }
}
