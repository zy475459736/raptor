package com.ppdai.framework.raptor.serialize;

import com.google.protobuf.Message;
import com.ppdai.framework.raptor.exception.RaptorServiceException;

public abstract class AbstractProtobufSerialization implements Serialization {
    //todo 最底层是基于protobuf协议，但是该方法设置为protected是否更好？
    public abstract byte[] serializeMessage(Message message);

    public abstract <T extends Message> T deserializeMessage(byte[] bytes, Class<T> clazz);

    @Override
    public byte[] serialize(Object obj) {
        if (obj instanceof Message) {
            return serializeMessage((Message) obj);
        }
        throw new RaptorServiceException(String.format("object [%s] is not protobuf Message, can not serialize.", obj));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (!Message.class.isAssignableFrom(clazz)) {
            throw new RaptorServiceException(String.format("class [%s] is not protobuf Message, can not serialize.", clazz.getName()));
        }
        Class<? extends Message> type = (Class<? extends Message>) clazz;
        return (T) deserializeMessage(bytes, type);
    }

}
