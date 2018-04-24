package com.ppdai.framework.raptor.proto;

import com.google.protobuf.WireFormat;

import java.util.List;
import java.util.Map;

/**
 * @author yinzuolong
 */
public class AllTypesPojos {

    public AllTypesPojos() {
    }

    public enum NestedEnum implements ProtoEnum {
        A(0);

        private final int value;

        NestedEnum(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    public static final class NestedMessage {

        @Protobuf(order = 1, fieldType = WireFormat.FieldType.INT32)
        public Integer a;
    }

    @Protobuf(order = 1, fieldType = WireFormat.FieldType.INT32)
    private Integer int32;

    @Protobuf(order = 2, fieldType = WireFormat.FieldType.UINT32)
    private Integer uint32;

    @Protobuf(order = 3, fieldType = WireFormat.FieldType.SINT32)
    private Integer sint32;

    @Protobuf(order = 4, fieldType = WireFormat.FieldType.FIXED32)
    private Integer fixed32;

    @Protobuf(order = 5, fieldType = WireFormat.FieldType.SFIXED32)
    private Integer sfixed32;

    @Protobuf(order = 6, fieldType = WireFormat.FieldType.INT64)
    private Long int64;

    @Protobuf(order = 7, fieldType = WireFormat.FieldType.UINT64)
    private Long uint64;

    @Protobuf(order = 8, fieldType = WireFormat.FieldType.SINT64)
    private Long sint64;

    @Protobuf(order = 9, fieldType = WireFormat.FieldType.FIXED64)
    private Long fixed64;

    @Protobuf(order = 10, fieldType = WireFormat.FieldType.SFIXED64)
    private Long sfixed64;

    @Protobuf(order = 11, fieldType = WireFormat.FieldType.BOOL)
    private Boolean bool;

    @Protobuf(order = 12, fieldType = WireFormat.FieldType.FLOAT)
    private Float floa;

    @Protobuf(order = 13, fieldType = WireFormat.FieldType.DOUBLE)
    private Double doub;

    @Protobuf(order = 14, fieldType = WireFormat.FieldType.STRING)
    private String string;

    @Protobuf(order = 15, fieldType = WireFormat.FieldType.BYTES)
    private byte[] bytes;

    @Protobuf(order = 16, fieldType = WireFormat.FieldType.ENUM)
    private NestedEnum nested_enum;

    @Protobuf(order = 17, fieldType = WireFormat.FieldType.MESSAGE)
    private NestedMessage nested_message;

    /*-----List--------*/
    @Protobuf(order = 201, fieldType = WireFormat.FieldType.INT32)
    private List<Integer> repInt32;

    @Protobuf(order = 202, fieldType = WireFormat.FieldType.UINT32)
    private List<Integer> repUint32;

    @Protobuf(order = 203, fieldType = WireFormat.FieldType.SINT32)
    private List<Integer> repSint32;

    @Protobuf(order = 204, fieldType = WireFormat.FieldType.FIXED32)
    private List<Integer> repFixed32;

    @Protobuf(order = 205, fieldType = WireFormat.FieldType.SFIXED32)
    private List<Integer> repSfixed32;

    @Protobuf(order = 206, fieldType = WireFormat.FieldType.INT64)
    private List<Long> repInt64;

    @Protobuf(order = 207, fieldType = WireFormat.FieldType.UINT64)
    private List<Long> repUint64;

    @Protobuf(order = 208, fieldType = WireFormat.FieldType.SINT64)
    private List<Long> repSint64;

    @Protobuf(order = 209, fieldType = WireFormat.FieldType.FIXED64)
    private List<Long> repFixed64;

    @Protobuf(order = 210, fieldType = WireFormat.FieldType.SFIXED64)
    private List<Long> repSfixed64;

    @Protobuf(order = 211, fieldType = WireFormat.FieldType.BOOL)
    private List<Boolean> repBool;

    @Protobuf(order = 212, fieldType = WireFormat.FieldType.FLOAT)
    private List<Float> repFloat;

    @Protobuf(order = 213, fieldType = WireFormat.FieldType.DOUBLE)
    private List<Double> repDouble;

    @Protobuf(order = 214, fieldType = WireFormat.FieldType.STRING)
    private List<String> repString;

    @Protobuf(order = 215, fieldType = WireFormat.FieldType.BYTES)
    private List<byte[]> repBytes;

    @Protobuf(order = 216, fieldType = WireFormat.FieldType.ENUM)
    private List<NestedEnum> repNestedEnum;

    @Protobuf(order = 217, fieldType = WireFormat.FieldType.MESSAGE)
    private List<NestedMessage> repNestedMessage;

    /*-----Map--------*/
    @Protobuf(order = 501, fieldType = WireFormat.FieldType.INT32, keyType = WireFormat.FieldType.INT32)
    private Map<Integer, Integer> mapInt32Int32;

    @Protobuf(order = 502, fieldType = WireFormat.FieldType.STRING)
    private Map<String, String> mapStringString;

    @Protobuf(order = 503, fieldType = WireFormat.FieldType.MESSAGE)
    private Map<String, NestedMessage> mapStringMessage;

    @Protobuf(order = 504, fieldType = WireFormat.FieldType.ENUM)
    private Map<String, NestedEnum> mapStringEnum;

}
