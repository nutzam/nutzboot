package org.nutz.boot.test;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;


/**
 * @author 邓华锋
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith(NutzBootExtension.class)
public @interface NutzBootTest {
    /**
     * Alias for {@link #properties()}.
     * @return the properties to apply
     */
    String[] value() default {};

    /**
     * Properties in form key=value that should be added to the Nutzboot
     * Environment before the test runs.
     * @return the properties to add
     */
    String[] properties() default {};

    /**
     * Application arguments that should be passed to the application under test.
     * @return the application arguments to pass to the application under test.
     */
    String[] args() default {};

    Class<?>[] classes() default {};

    /**
     * The type of web environment to create when applicable. Defaults to
     * {@link WebEnvironment#MOCK}.
     * @return the type of web environment
     */
    WebEnvironment webEnvironment() default WebEnvironment.MOCK;

    /**
     * 测试机哦u偶下
     */
    enum WebEnvironment {
        /**
         * mock
         */
        MOCK(false),

        /**
         * 随机端口
         */
        RANDOM_PORT(true),


        /**
         * defina port
         */
        DEFINED_PORT(true),

        /**
         * 默认配置
         */
        NONE(false);


        /**
         * 是否tomcat
         */
        private final boolean embedded;

        /**
         * 是否tomcat
         * @param embedded
         */
        WebEnvironment(boolean embedded) {
            this.embedded = embedded;
        }

        /**
         * 获取环境
         * @return
         */
        public boolean isEmbedded() {
            return this.embedded;
        }

    }
}
