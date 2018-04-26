package com.ppdai.framework.raptor.proto;

import com.google.protobuf.WireFormat;
import com.ppdai.framework.raptor.annotation.ProtoField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author yinzuolong
 */
@Getter
@Setter
public class AllTypesPojos {

    public AllTypesPojos() {
    }

    public enum NestedEnum {
        A(0);

        private final int value;

        NestedEnum(int value) {
            this.value = value;
        }
    }

    public static final class NestedMessage {
        @ProtoField(order = 1, fieldType = WireFormat.FieldType.INT32)
        public Integer a;
    }

    @ProtoField(order = 1, fieldType = WireFormat.FieldType.INT32)
    private Integer int32;

    @ProtoField(order = 2, fieldType = WireFormat.FieldType.UINT32)
    private Integer uint32;

    @ProtoField(order = 3, fieldType = WireFormat.FieldType.SINT32)
    private Integer sint32;

    @ProtoField(order = 4, fieldType = WireFormat.FieldType.FIXED32)
    private Integer fixed32;

    @ProtoField(order = 5, fieldType = WireFormat.FieldType.SFIXED32)
    private Integer sfixed32;

    @ProtoField(order = 6, fieldType = WireFormat.FieldType.INT64)
    private Long int64;

    @ProtoField(order = 7, fieldType = WireFormat.FieldType.UINT64)
    private Long uint64;

    @ProtoField(order = 8, fieldType = WireFormat.FieldType.SINT64)
    private Long sint64;

    @ProtoField(order = 9, fieldType = WireFormat.FieldType.FIXED64)
    private Long fixed64;

    @ProtoField(order = 10, fieldType = WireFormat.FieldType.SFIXED64)
    private Long sfixed64;

    @ProtoField(order = 11, fieldType = WireFormat.FieldType.BOOL)
    private Boolean bool;

    @ProtoField(order = 12, fieldType = WireFormat.FieldType.FLOAT)
    private Float floa;

    @ProtoField(order = 13, fieldType = WireFormat.FieldType.DOUBLE)
    private Double doub;

    @ProtoField(order = 14, fieldType = WireFormat.FieldType.STRING)
    private String string;

    @ProtoField(order = 15, fieldType = WireFormat.FieldType.BYTES)
    private byte[] bytes;

    @ProtoField(order = 16, fieldType = WireFormat.FieldType.ENUM)
    private NestedEnum nested_enum;

    @ProtoField(order = 17, fieldType = WireFormat.FieldType.MESSAGE)
    private NestedMessage nested_message;

    /*-----List--------*/
    @ProtoField(order = 201, repeated = true, fieldType = WireFormat.FieldType.INT32)
    private List<Integer> repInt32;

    @ProtoField(order = 202, repeated = true, fieldType = WireFormat.FieldType.UINT32)
    private List<Integer> repUint32;

    @ProtoField(order = 203, repeated = true, fieldType = WireFormat.FieldType.SINT32)
    private List<Integer> repSint32;

    @ProtoField(order = 204, repeated = true, fieldType = WireFormat.FieldType.FIXED32)
    private List<Integer> repFixed32;

    @ProtoField(order = 205, repeated = true, fieldType = WireFormat.FieldType.SFIXED32)
    private List<Integer> repSfixed32;

    @ProtoField(order = 206, repeated = true, fieldType = WireFormat.FieldType.INT64)
    private List<Long> repInt64;

    @ProtoField(order = 207, repeated = true, fieldType = WireFormat.FieldType.UINT64)
    private List<Long> repUint64;

    @ProtoField(order = 208, repeated = true, fieldType = WireFormat.FieldType.SINT64)
    private List<Long> repSint64;

    @ProtoField(order = 209, repeated = true, fieldType = WireFormat.FieldType.FIXED64)
    private List<Long> repFixed64;

    @ProtoField(order = 210, repeated = true, fieldType = WireFormat.FieldType.SFIXED64)
    private List<Long> repSfixed64;

    @ProtoField(order = 211, repeated = true, fieldType = WireFormat.FieldType.BOOL)
    private List<Boolean> repBool;

    @ProtoField(order = 212, repeated = true, fieldType = WireFormat.FieldType.FLOAT)
    private List<Float> repFloat;

    @ProtoField(order = 213, repeated = true, fieldType = WireFormat.FieldType.DOUBLE)
    private List<Double> repDouble;

    @ProtoField(order = 214, repeated = true, fieldType = WireFormat.FieldType.STRING)
    private List<String> repString;

    @ProtoField(order = 215, repeated = true, fieldType = WireFormat.FieldType.BYTES)
    private List<byte[]> repBytes;

    @ProtoField(order = 216, repeated = true, fieldType = WireFormat.FieldType.ENUM)
    private List<NestedEnum> repNestedEnum;

    @ProtoField(order = 217, repeated = true, fieldType = WireFormat.FieldType.MESSAGE)
    private List<NestedMessage> repNestedMessage;

    /*-----Map--------*/
    @ProtoField(order = 501, isMap = true, fieldType = WireFormat.FieldType.INT32, keyType = WireFormat.FieldType.INT32)
    private Map<Integer, Integer> mapInt32Int32;

    @ProtoField(order = 502, isMap = true, fieldType = WireFormat.FieldType.STRING)
    private Map<String, String> mapStringString;

    @ProtoField(order = 503, isMap = true, fieldType = WireFormat.FieldType.MESSAGE)
    private Map<String, NestedMessage> mapStringMessage;

    @ProtoField(order = 504, isMap = true, fieldType = WireFormat.FieldType.ENUM)
    private Map<String, NestedEnum> mapStringEnum;

    /*------oneof--------*/
    //TODO

}
