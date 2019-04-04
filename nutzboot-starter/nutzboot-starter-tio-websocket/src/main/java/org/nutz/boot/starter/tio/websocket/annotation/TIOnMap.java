package org.nutz.boot.starter.tio.websocket.annotation;

import java.lang.annotation.*;

/**
 * example:
 *
 * import org.tio.core.ChannelContext;
 *
 * \@TIOnMap
 * public void onMapDefault(ChannelContext channelContext, String event, AnyObject object) {}
 *
 * \@TIOnMap("ping")
 * public void onMap(ChannelContext channelContext, AnyObject object) {}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface TIOnMap {
    String value() default "";
}
