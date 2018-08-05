package com.ppdai.framework.raptor.serialize;
/**
 * 编码解码的接口
 * */
public interface Serialization {

    String getName();

    byte[] serialize(Object obj);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
