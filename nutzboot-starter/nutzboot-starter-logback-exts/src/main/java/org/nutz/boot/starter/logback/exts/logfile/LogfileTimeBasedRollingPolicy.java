package org.nutz.boot.starter.logback.exts.logfile;

import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import org.nutz.lang.Lang;

public class LogfileTimeBasedRollingPolicy<E> extends TimeBasedRollingPolicy<E> {
    @Override
    public void start() {
        String fileNamePattern = super.getFileNamePattern();
        if (fileNamePattern.endsWith(".log") || fileNamePattern.endsWith(".LOG")) {
            fileNamePattern = fileNamePattern.substring(0, fileNamePattern.length() - 4) + "-" + Lang.JdkTool.getProcessId("0") + ".log";
        } else {
            fileNamePattern = fileNamePattern + "-" + Lang.JdkTool.getProcessId("0") + ".log";
        }
        super.setFileNamePattern(fileNamePattern);
        super.start();
    }
}
