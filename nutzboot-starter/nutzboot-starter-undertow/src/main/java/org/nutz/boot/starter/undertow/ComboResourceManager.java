package org.nutz.boot.starter.undertow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Streams;

import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;

public class ComboResourceManager implements ResourceManager {
    
    protected List<ResourceManager> managers = new ArrayList<>();

    public void close() throws IOException {
        managers.forEach((action)->Streams.safeClose(action));
    }

    public Resource getResource(String path) throws IOException {
        for (ResourceManager manager : managers) {
            Resource resource = manager.getResource(path);
            if (resource != null)
                return resource;
        }
        return null;
    }

    public boolean isResourceChangeListenerSupported() {
        for (ResourceManager manager : managers) {
            if (manager.isResourceChangeListenerSupported())
                return true;
        }
        return false;
    }

    public void registerResourceChangeListener(ResourceChangeListener listener) {
        for (ResourceManager manager : managers) {
            if (manager.isResourceChangeListenerSupported())
                manager.registerResourceChangeListener(listener);
        }
    }

    public void removeResourceChangeListener(ResourceChangeListener listener) {
        for (ResourceManager manager : managers) {
            if (manager.isResourceChangeListenerSupported())
                manager.registerResourceChangeListener(listener);
        }
    }

    public void add(ResourceManager manager) {
        managers.add(manager);
    }

    public void remove(ResourceManager manager) {
        managers.remove(manager);
    }
    
    public void clear() {
        managers.clear();
    }
}
