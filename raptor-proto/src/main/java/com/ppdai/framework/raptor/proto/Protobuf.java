package com.ppdai.framework.raptor.proto;

import com.google.protobuf.WireFormat;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Protobuf {

    int order();

    WireFormat.FieldType fieldType();

    /**
     * Specifying Field description which comment end of field define like:
     * int32 count = 10; //count
     * @return
     */
    String description() default "";

}
