package org.nutz.boot.starter;

/**
 * 带启动和关闭的"Server"
 * 
 * @author Administrator
 *
 */
public interface ServerFace {

    /**
     * 启动吧骚年
     */
    void start() throws Exception;

    /**
     * 关闭,再见
     */
    default void stop() throws Exception {}

    /**
     * 是否正在运行,预留,未实行
     */
    default boolean isRunning() {
        return true;
    }

    /**
     * 如果启动报错,是否继续运行,预留,未实行
     * 
     * @return true 默认
     */
    default boolean failsafe() {
        return true;
    }
}
