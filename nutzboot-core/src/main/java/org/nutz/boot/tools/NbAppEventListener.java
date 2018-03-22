package org.nutz.boot.tools;

import org.nutz.boot.NbApp;

public interface NbAppEventListener {

    default void whenPrepare(NbApp app, EventType et) {};
    
    default void whenPrepareBasic(NbApp app, EventType et) {};
    
    default void whenPrepareConfigureLoader(NbApp app, EventType et) {};
    
    default void whenPrepareIocLoader(NbApp app, EventType et) {};
    
    default void whenPrepareStarterClassList(NbApp app, EventType et) {};
    
    default void whenPrepareIoc(NbApp app, EventType et) {};
    
    default void whenPrepareStarterInstance(NbApp app, EventType et) {};
    
    default void whenInitAppContext(NbApp app, EventType et) {};
    
    default void whenStartServers(NbApp app, EventType et) {}
    
    default void afterAppStated(NbApp app) {};
    
    enum EventType {
        before, after
    }
}
