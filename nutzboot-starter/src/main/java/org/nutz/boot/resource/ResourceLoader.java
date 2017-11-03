package org.nutz.boot.resource;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceLoader {

    boolean has(String path) throws IOException;
    
    InputStream get(String path) throws IOException;
}
