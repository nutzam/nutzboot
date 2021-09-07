package org.nutz.boot.starter.swagger3.annotation;

import java.lang.annotation.*;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/2/16
 */
@Target(ElementType.PARAMETER)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiFormParam {
    String name() default "";
    String type() default "string";
    String format() default "";
    String description() default "";
    String example() default "";
    boolean required() default false;
}
