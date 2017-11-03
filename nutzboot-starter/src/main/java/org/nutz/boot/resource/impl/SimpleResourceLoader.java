package org.nutz.boot.resource.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.nutz.boot.aware.ClassLoaderAware;
import org.nutz.boot.resource.ResourceLoader;

public class SimpleResourceLoader implements ResourceLoader, ClassLoaderAware {
    
    protected ClassLoader classLoader;
    
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public boolean has(String path) {
        File f = new File(path);
        if (f.exists())
            return true;
        if (classLoader != null)
            return classLoader.getResource(path) != null;
        return getClass().getClassLoader().getResource(path) != null;
    }

    public InputStream get(String path) throws IOException {
        File f = new File(path);
        if (f.exists())
            return new FileInputStream(f);
        if (classLoader != null)
            return classLoader.getResourceAsStream(path);
        return getClass().getClassLoader().getResourceAsStream(path);
    }

}
