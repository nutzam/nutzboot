package org.nutz.boot.ioc;

import org.nutz.ioc.IocLoader;

/**
 * 供Starter实现的接口,用于提供IocLoader,加入到全局Ioc容器中
 *
 */
public interface IocLoaderProvider {

    IocLoader getIocLoader();
}
