package org.nutz.boot.ioc;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocObject;

public class NbIocLoader implements IocLoader {

    public String[] getName() {
        return null;
    }

    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
        return null;
    }

    public boolean has(String name) {
        return false;
    }

    
}
