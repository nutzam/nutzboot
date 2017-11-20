package org.nutz.log.impl;

import org.nutz.log.Log;
import org.nutz.log.LogAdapter;
import org.nutz.plugin.Plugin;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

/**
 * @author wendal
 *
 */
public class Slf4jLogAdapter implements LogAdapter, Plugin {

	public Log getLogger(String className) {
		return new Slf4jLogger((LocationAwareLogger) LoggerFactory.getLogger(className));
	}

	public boolean canWork() {
		try {
			this.getLogger(getClass().getName()).info("Using Slf4jLogger");
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}

}
