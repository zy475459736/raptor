package com.ppdai.codegen.demo.wire.demo.swagger;

import java.lang.annotation.*;

/**
 * @author zhangchengxi
 * Date 2018/4/24
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Tag {

    int value();
}
