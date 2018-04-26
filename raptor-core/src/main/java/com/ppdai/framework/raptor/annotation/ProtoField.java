package com.ppdai.framework.raptor.annotation;

import com.google.protobuf.WireFormat;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ProtoField {

    int order();

    String name() default "";

    WireFormat.FieldType fieldType();

    WireFormat.FieldType keyType() default WireFormat.FieldType.STRING;

    boolean repeated() default false;

    boolean isMap() default false;

    boolean oneof() default false;

    /**
     * Specifying Field summary which comment end of field define like:
     * int32 count = 10; //count
     *
     * @return
     */
    String summary() default "";

}
