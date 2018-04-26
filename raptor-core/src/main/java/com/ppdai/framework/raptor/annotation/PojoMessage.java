package com.ppdai.framework.raptor.annotation;

import java.lang.annotation.*;

/**
 * @author yinzuolong
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PojoMessage {

    String version() default "";

    String protoFile() default "";

    String crc32() default "";

    String summary() default "";
}
