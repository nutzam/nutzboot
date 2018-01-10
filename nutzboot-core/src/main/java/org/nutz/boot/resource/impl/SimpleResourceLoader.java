package org.nutz.boot.resource.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.nutz.boot.aware.ClassLoaderAware;
import org.nutz.boot.resource.ResourceLoader;

/**
 * 内置的简单版资源加载器,简单来说就是从本地路径和classpath找资源
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class SimpleResourceLoader implements ResourceLoader, ClassLoaderAware {
    
    /**
     * ClassLoader嗷嗷嗷
     */
    protected ClassLoader classLoader;
    
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public boolean has(String path) {
        // 先找文件
        File f = new File(path);
        if (f.exists())
            return true;
        // 再找classpath
        if (classLoader != null)
            return classLoader.getResource(path) != null;
        return getClass().getClassLoader().getResource(path) != null;
    }

    public InputStream get(String path) throws IOException {
        File f = new File(path);
        if (f.exists() && f.canRead())
            return new FileInputStream(f);
        if (classLoader != null)
            return classLoader.getResourceAsStream(path);
        return getClass().getClassLoader().getResourceAsStream(path);
    }

}
