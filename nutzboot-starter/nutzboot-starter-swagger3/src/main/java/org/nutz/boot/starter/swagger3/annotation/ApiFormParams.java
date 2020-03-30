package org.nutz.boot.starter.swagger3.annotation;

import java.lang.annotation.*;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/2/17
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ApiFormParams {
    ApiFormParam[] apiFormParams() default {};

    Class<?> implementation() default Void.class;

    String mediaType() default "application/x-www-form-urlencoded";
}
