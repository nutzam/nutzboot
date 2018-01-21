package org.nutz.cloud.config.spi;

import java.util.EventListener;
import java.util.List;

public interface ConfigureEventHandler extends EventListener {

    void trigger(List<KeyEvent> events);
    
    class KeyEvent {
        public String name;
        public String value;
        public String action;
    }
}
