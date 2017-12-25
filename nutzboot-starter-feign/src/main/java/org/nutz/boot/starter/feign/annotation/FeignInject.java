package org.nutz.boot.starter.feign.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignInject {

    String apiBaseUrl() default "";

    String encoder() default "jackson";

    String decoder() default "jackson";
}
