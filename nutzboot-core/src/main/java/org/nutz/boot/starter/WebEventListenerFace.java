package org.nutz.boot.starter;

import java.util.EventListener;

/**
 * 简单来说,就是提供一个ServletContextEventListener
 *
 */
public interface WebEventListenerFace {

    /**
     * 通常是ServletContextEventListener实例,可以是null
     * 
     * @return ServletContextEventListener实例
     */
    EventListener getEventListener();
}
