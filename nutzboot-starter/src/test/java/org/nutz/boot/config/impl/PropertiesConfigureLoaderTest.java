package org.nutz.boot.config.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.ioc.impl.PropertiesProxy;

public class PropertiesConfigureLoaderTest {

	@Test
	public void test_from_args() {
		PropertiesConfigureLoader configureLoader = new PropertiesConfigureLoader();
		configureLoader.parseCommandLineArgs(configureLoader.get(), "--debug --jetty.port=8181 --jetty.host 127.0.0.2 --nutz.profiles.active=test".split(" "));
		PropertiesProxy conf = configureLoader.get();
		System.out.println(conf.toMap());
		assertEquals(4, conf.size());
		assertEquals("true", conf.get("debug"));
		assertEquals(8181, conf.getInt("jetty.port"));
		assertEquals("127.0.0.2", conf.get("jetty.host"));
	}
}
