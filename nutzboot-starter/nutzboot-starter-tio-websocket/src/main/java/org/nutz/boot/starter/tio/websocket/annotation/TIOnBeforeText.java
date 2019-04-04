package org.nutz.boot.starter.tio.websocket.annotation;

import java.lang.annotation.*;

/**
 * example:
 *
 * import org.tio.core.ChannelContext;
 *
 * \@TIOnBeforeText
 * public TioWebsocketRequest onBeforeText(ChannelContext channelContext, String text) {
 *     return TioWebsocketRequest.builder().event("ping").object("any").build
 * }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface TIOnBeforeText {
}
