package org.nutz.boot.starter.tomcat;

import org.apache.catalina.loader.WebappClassLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;

public class TomcatWebappClassLoader extends WebappClassLoader {

	private static final Log logger = LogFactory.getLog(TomcatWebappClassLoader.class);

	public TomcatWebappClassLoader() {
		super();
	}

	public TomcatWebappClassLoader(ClassLoader parent) {
		super(parent);
	}

	@Override
	public synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		Class<?> result = findExistingLoadedClass(name);
		result = (result == null ? doLoadClass(name) : result);
		if (result == null) {
			throw new ClassNotFoundException(name);
		}
		return resolveIfNecessary(result, resolve);
	}

	private Class<?> findExistingLoadedClass(String name) {
		Class<?> resultClass = findLoadedClass0(name);
		resultClass = (resultClass == null ? findLoadedClass(name) : resultClass);
		return resultClass;
	}

	private Class<?> doLoadClass(String name) throws ClassNotFoundException {
		checkPackageAccess(name);
		if ((this.delegate || filter(name, true))) {
			Class<?> result = loadFromParent(name);
			return (result == null ? findClassIgnoringNotFound(name) : result);
		}
		Class<?> result = findClassIgnoringNotFound(name);
		return (result == null ? loadFromParent(name) : result);
	}

	private Class<?> resolveIfNecessary(Class<?> resultClass, boolean resolve) {
		if (resolve) {
			resolveClass(resultClass);
		}
		return (resultClass);
	}

	@Override
	protected void addURL(URL url) {
		// Ignore URLs added by the Tomcat 8 implementation (see gh-919)
		if (logger.isTraceEnabled()) {
			logger.trace("Ignoring request to add " + url + " to the tomcat classloader");
		}
	}

	private Class<?> loadFromParent(String name) {
		if (this.parent == null) {
			return null;
		}
		try {
			return Class.forName(name, false, this.parent);
		}
		catch (ClassNotFoundException ex) {
			return null;
		}
	}

	private Class<?> findClassIgnoringNotFound(String name) {
		try {
			return findClass(name);
		}
		catch (ClassNotFoundException ex) {
			return null;
		}
	}

	private void checkPackageAccess(String name) throws ClassNotFoundException {
		if (this.securityManager != null && name.lastIndexOf('.') >= 0) {
			try {
				this.securityManager
						.checkPackageAccess(name.substring(0, name.lastIndexOf('.')));
			}
			catch (SecurityException ex) {
				throw new ClassNotFoundException("Security Violation, attempt to use "
						+ "Restricted Class: " + name, ex);
			}
		}
	}

}
