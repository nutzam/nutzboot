package org.nutz.boot.starter.tomcat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nutz.lang.Files;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;


public abstract class AbstractServletContainerFactory {

	protected final Log logger = LogFactory.getLog(getClass());

	private static final String[] COMMON_DOC_ROOTS = { "webapp", "public","static" };

	private File documentRoot;

	public AbstractServletContainerFactory() {
		super();
	}

	public File getDocumentRoot() {
		return this.documentRoot;
	}

	/**
	 * Returns the absolute document root when it points to a valid directory, logging a
	 * warning and returning {@code null} otherwise.
	 * @return the valid document root
	 */
	protected final File getValidDocumentRoot(String staticPath) {
		File file = getDocumentRoot();
		// If document root not explicitly set see if we are running from a war archive
		file = file != null ? file : getWarFileDocumentRoot();
		// If not a war archive maybe it is an exploded war
		file = file != null ? file : getExplodedWarFileDocumentRoot();
		// Or maybe there is a document root in a well-known location
		file = file != null ? file : getCommonDocumentRoot();
		//static file
		file = file != null ? file : Files.findFile(staticPath);
		if (file == null && this.logger.isDebugEnabled()) {
			this.logger
					.debug("None of the document roots " + Arrays.asList(COMMON_DOC_ROOTS)
							+ " point to a directory and will be ignored.");
		}
		else if (this.logger.isDebugEnabled()) {
			this.logger.debug("Document root: " + file);
		}
		return file;
	}

	private File getExplodedWarFileDocumentRoot() {
		return getExplodedWarFileDocumentRoot(getCodeSourceArchive());
	}

	protected List<URL> getUrlsOfJarsWithMetaInfResources() {
		ClassLoader classLoader = getClass().getClassLoader();
		List<URL> staticResourceUrls = new ArrayList<URL>();
		if (classLoader instanceof URLClassLoader) {
			for (URL url : ((URLClassLoader) classLoader).getURLs()) {
				try {
					if ("file".equals(url.getProtocol())) {
						File file = new File(url.getFile());
						if (file.isDirectory()
								&& new File(file, "META-INF/resources").isDirectory()) {
							staticResourceUrls.add(url);
						}
						else if (isResourcesJar(file)) {
							staticResourceUrls.add(url);
						}
					}
					else {
						URLConnection connection = url.openConnection();
						if (connection instanceof JarURLConnection) {
							if (isResourcesJar((JarURLConnection) connection)) {
								staticResourceUrls.add(url);
							}
						}
					}
				}
				catch (IOException ex) {
					throw new IllegalStateException(ex);
				}
			}
		}
		return staticResourceUrls;
	}

	private boolean isResourcesJar(JarURLConnection connection) {
		try {
			return isResourcesJar(connection.getJarFile());
		}
		catch (IOException ex) {
			return false;
		}
	}

	private boolean isResourcesJar(File file) {
		try {
			return isResourcesJar(new JarFile(file));
		}
		catch (IOException ex) {
			return false;
		}
	}

	private boolean isResourcesJar(JarFile jar) throws IOException {
		try {
			return jar.getName().endsWith(".jar")
					&& (jar.getJarEntry("META-INF/resources") != null);
		}
		finally {
			jar.close();
		}
	}

	File getExplodedWarFileDocumentRoot(File codeSourceFile) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Code archive: " + codeSourceFile);
		}
		if (codeSourceFile != null && codeSourceFile.exists()) {
			String path = codeSourceFile.getAbsolutePath();
			int webInfPathIndex = path
					.indexOf(File.separatorChar + "WEB-INF" + File.separatorChar);
			if (webInfPathIndex >= 0) {
				path = path.substring(0, webInfPathIndex);
				return new File(path);
			}
		}
		return null;
	}

	private File getWarFileDocumentRoot() {
		return getArchiveFileDocumentRoot(".war");
	}

	private File getArchiveFileDocumentRoot(String extension) {
		File file = getCodeSourceArchive();
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Code archive: " + file);
		}
		if (file != null && file.exists() && !file.isDirectory()
				&& file.getName().toLowerCase().endsWith(extension)) {
			return file.getAbsoluteFile();
		}
		return null;
	}

	private File getCommonDocumentRoot() {
		for (String commonDocRoot : COMMON_DOC_ROOTS) {
			URL url = Thread.currentThread().getContextClassLoader().getResource(commonDocRoot);
			if (url!=null) {
				File root = new File(url.getPath());
				if (root.exists() && root.isDirectory()) {
					return root.getAbsoluteFile();
				}
			}
		}
		return null;
	}

	private File getCodeSourceArchive() {
		return getCodeSourceArchive(getClass().getProtectionDomain().getCodeSource());
	}

	File getCodeSourceArchive(CodeSource codeSource) {
		try {
			URL location = (codeSource == null ? null : codeSource.getLocation());
			if (location == null) {
				return null;
			}
			String path;
			URLConnection connection = location.openConnection();
			if (connection instanceof JarURLConnection) {
				path = ((JarURLConnection) connection).getJarFile().getName();
			}
			else {
				path = location.toURI().getPath();
			}
			if (path.contains("!/")) {
				path = path.substring(0, path.indexOf("!/"));
			}
			return new File(path);
		}
		catch (Exception ex) {
			return null;
		}
	}

	protected final File getValidSessionStoreDir(boolean mkdirs) {
		return new ApplicationTemp().getDir("servlet-sessions");
	}

}
