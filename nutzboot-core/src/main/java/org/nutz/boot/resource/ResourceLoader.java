package org.nutz.boot.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * 在NbApp启动过程中加载资源的方法
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public interface ResourceLoader {

    /**
     * 是否存在某个路径的资源
     * 
     * @param path
     *            路径
     * @return true,如果存在的话
     * @throws IOException
     */
    boolean has(String path) throws IOException;

    /**
     * 根据路径获取资源
     * 
     * @param path
     *            路径
     * @return 资源
     * @throws IOException
     */
    InputStream get(String path) throws IOException;
}
