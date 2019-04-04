package org.nutz.boot.starter.tio.websocket.annotation;

import java.lang.annotation.*;

/**
 * example:
 *
 * import org.tio.core.ChannelContext;
 *
 * \@TIOnClose
 * public void onClose(ChannelContext channelContext) {}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface TIOnClose {
}
