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

    String encoder() default "";

    String decoder() default "";

    /**
     * JAXB作为编码/解码器的时候必须填写
     */
    String schema() default "";

    /**
     * 客户端用什么
     */
    String client() default "";

    /**
     * 是否启用feign-hystrix
     * @return
     */
    String useHystrix() default "";

    String fallback() default "";
    
    /**
     * 负载均衡规则
     */
    String lbRule() default "";
    
    /**
     * 专属nutz.json的JsonFormat
     * @return full/forLook/.... 或者 一个json字符串
     */
    String jsonFormat() default "";
}
