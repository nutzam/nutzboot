package org.nutz.boot.starter.prevent.duplicate.submit.annotation;

import java.lang.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/8/14
 * 描述此类：
 * Token必须是成对出现的，一个创建，一个移除
 * <p>
 * 在Remove类型下捕获到任何异常都将恢复Token
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Token {
    /**
     * 类型：创建Token或者移除Token
     *
     * @return
     */
    Type type() default Type.CREATE;

    /**
     * 访问地址
     * <p>
     * 为空则自动获取当前地址，创建Token和移除Token的地址必须一致
     *
     * @return
     */
    String path() default "";

}
