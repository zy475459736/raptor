package com.ppdai.framework.raptor.proto;

import com.google.protobuf.WireFormat;
import com.ppdai.framework.raptor.annotation.RaptorMessage;
import com.ppdai.framework.raptor.annotation.RaptorField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author yinzuolong
 */
@Getter
@Setter
@RaptorMessage
public class AllTypesPojo {

    public AllTypesPojo() {
    }

    public enum NestedEnum {
        A(0);

        private final int value;

        NestedEnum(int value) {
            this.value = value;
        }
    }

    public static final class NestedMessage {
        @RaptorField(order = 1, fieldType = WireFormat.FieldType.INT32)
        public Integer a;
    }

    @RaptorField(order = 1, fieldType = WireFormat.FieldType.INT32)
    private Integer int32;

    @RaptorField(order = 2, fieldType = WireFormat.FieldType.UINT32)
    private Integer uint32;

    @RaptorField(order = 3, fieldType = WireFormat.FieldType.SINT32)
    private Integer sint32;

    @RaptorField(order = 4, fieldType = WireFormat.FieldType.FIXED32)
    private Integer fixed32;

    @RaptorField(order = 5, fieldType = WireFormat.FieldType.SFIXED32)
    private Integer sfixed32;

    @RaptorField(order = 6, fieldType = WireFormat.FieldType.INT64)
    private Long int64;

    @RaptorField(order = 7, fieldType = WireFormat.FieldType.UINT64)
    private Long uint64;

    @RaptorField(order = 8, fieldType = WireFormat.FieldType.SINT64)
    private Long sint64;

    @RaptorField(order = 9, fieldType = WireFormat.FieldType.FIXED64)
    private Long fixed64;

    @RaptorField(order = 10, fieldType = WireFormat.FieldType.SFIXED64)
    private Long sfixed64;

    @RaptorField(order = 11, fieldType = WireFormat.FieldType.BOOL)
    private Boolean bool;

    @RaptorField(order = 12, fieldType = WireFormat.FieldType.FLOAT)
    private Float floa;

    @RaptorField(order = 13, fieldType = WireFormat.FieldType.DOUBLE)
    private Double doub;

    @RaptorField(order = 14, fieldType = WireFormat.FieldType.STRING)
    private String string;

    @RaptorField(order = 15, fieldType = WireFormat.FieldType.BYTES)
    private byte[] bytes;

    @RaptorField(order = 16, fieldType = WireFormat.FieldType.ENUM)
    private NestedEnum nested_enum;

    @RaptorField(order = 17, fieldType = WireFormat.FieldType.MESSAGE)
    private NestedMessage nested_message;

    /*-----List--------*/
    @RaptorField(order = 201, repeated = true, fieldType = WireFormat.FieldType.INT32)
    private List<Integer> repInt32;

    @RaptorField(order = 202, repeated = true, fieldType = WireFormat.FieldType.UINT32)
    private List<Integer> repUint32;

    @RaptorField(order = 203, repeated = true, fieldType = WireFormat.FieldType.SINT32)
    private List<Integer> repSint32;

    @RaptorField(order = 204, repeated = true, fieldType = WireFormat.FieldType.FIXED32)
    private List<Integer> repFixed32;

    @RaptorField(order = 205, repeated = true, fieldType = WireFormat.FieldType.SFIXED32)
    private List<Integer> repSfixed32;

    @RaptorField(order = 206, repeated = true, fieldType = WireFormat.FieldType.INT64)
    private List<Long> repInt64;

    @RaptorField(order = 207, repeated = true, fieldType = WireFormat.FieldType.UINT64)
    private List<Long> repUint64;

    @RaptorField(order = 208, repeated = true, fieldType = WireFormat.FieldType.SINT64)
    private List<Long> repSint64;

    @RaptorField(order = 209, repeated = true, fieldType = WireFormat.FieldType.FIXED64)
    private List<Long> repFixed64;

    @RaptorField(order = 210, repeated = true, fieldType = WireFormat.FieldType.SFIXED64)
    private List<Long> repSfixed64;

    @RaptorField(order = 211, repeated = true, fieldType = WireFormat.FieldType.BOOL)
    private List<Boolean> repBool;

    @RaptorField(order = 212, repeated = true, fieldType = WireFormat.FieldType.FLOAT)
    private List<Float> repFloat;

    @RaptorField(order = 213, repeated = true, fieldType = WireFormat.FieldType.DOUBLE)
    private List<Double> repDouble;

    @RaptorField(order = 214, repeated = true, fieldType = WireFormat.FieldType.STRING)
    private List<String> repString;

    @RaptorField(order = 215, repeated = true, fieldType = WireFormat.FieldType.BYTES)
    private List<byte[]> repBytes;

    @RaptorField(order = 216, repeated = true, fieldType = WireFormat.FieldType.ENUM)
    private List<NestedEnum> repNestedEnum;

    @RaptorField(order = 217, repeated = true, fieldType = WireFormat.FieldType.MESSAGE)
    private List<NestedMessage> repNestedMessage;

    /*-----Map--------*/
    @RaptorField(order = 501, isMap = true, fieldType = WireFormat.FieldType.INT32, keyType = WireFormat.FieldType.INT32)
    private Map<Integer, Integer> mapInt32Int32;

    @RaptorField(order = 502, isMap = true, fieldType = WireFormat.FieldType.STRING)
    private Map<String, String> mapStringString;

    @RaptorField(order = 503, isMap = true, fieldType = WireFormat.FieldType.MESSAGE)
    private Map<String, NestedMessage> mapStringMessage;

    @RaptorField(order = 504, isMap = true, fieldType = WireFormat.FieldType.ENUM)
    private Map<String, NestedEnum> mapStringEnum;

    /*------oneof--------*/
    //TODO

}
