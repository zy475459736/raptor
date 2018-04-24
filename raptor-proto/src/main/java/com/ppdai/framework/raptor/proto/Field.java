package com.ppdai.framework.raptor.proto;

import com.google.protobuf.WireFormat;
import lombok.Getter;

@Getter
public class Field {

    private final int order;
    private final WireFormat.FieldType type;
    private final Class<?> javaType;
    private final String name;
    private final boolean repeated;

    public Field(int order, WireFormat.FieldType type, Class<?> javaType, String name, boolean repeated) {
        this.order = order;
        this.type = type;
        this.javaType = javaType;
        this.name = name;
        this.repeated = repeated;
    }
}
