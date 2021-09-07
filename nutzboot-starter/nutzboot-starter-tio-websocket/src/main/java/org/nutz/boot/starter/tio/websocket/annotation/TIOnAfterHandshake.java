package org.nutz.boot.starter.tio.websocket.annotation;
import java.lang.annotation.*;

/**
 * example:
 *
 * import org.tio.http.common.HttpRequest;
 * import org.tio.http.common.HttpResponse;
 * import org.tio.core.ChannelContext;
 *
 * \@TIOnAfterHandshake
 * public void onAfterHandshake(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) {}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface TIOnAfterHandshake {
}
