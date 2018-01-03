package org.nutz.boot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface PropDoc {

    /**
     * 配置信息的前缀,貌似没啥用,预留
     */
    String prefix() default "";

    /**
     * 配置信息的名字, 例如 nutz.application.name=nutzboot
     */
    String key() default "";

    /**
     * 本配置信息的描述,给人看的
     */
    String value() default "";

    /**
     * 配置数据的java类型,例如int,long,String
     */
    String type() default "";

    /**
     * 属于哪个组的配置信息,预留
     */
    String group() default "";

    /**
     * 可选值列表
     */
    String[] possible() default "";

    /**
     * 默认值
     * 
     * @return 默认木有
     */
    String defaultValue() default "";

    /**
     * 是否必填
     * 
     * @return 默认false
     */
    boolean need() default false;
}
