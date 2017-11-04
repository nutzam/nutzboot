package org.nutz.boot.starter;

public interface ServerFace {

    void start() throws Exception;
    
    void stop() throws Exception;
    
    boolean isRunning();
    
    boolean failsafe();
}
