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

	String prefix() default "";
	String key() default "";
	String value() default "";
	String type() default "";
	String group() default "";
	String[] possible() default "";
	String defaultValue() default "";
	boolean need() default false;
}
