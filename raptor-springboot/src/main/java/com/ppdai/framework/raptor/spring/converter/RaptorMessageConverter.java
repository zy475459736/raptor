package com.ppdai.framework.raptor.spring.converter;

import com.ppdai.framework.raptor.annotation.RaptorMessage;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author yinzuolong
 */
public class RaptorMessageConverter extends AbstractHttpMessageConverter<Object> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final MediaType PROTOBUF = new MediaType("application", "x-protobuf", DEFAULT_CHARSET);

    public RaptorMessageConverter() {
        super(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        MediaType contentType = outputMessage.getHeaders().getContentType();
        if (contentType == null) {
            contentType = PROTOBUF;
        }
        Charset charset = contentType.getCharset();
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }

        if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            //TODO json序列化
        } else {
            //TODO protobuf序列化
        }
    }


    @Override
    protected boolean supports(Class clazz) {
        RaptorMessage annotation = AnnotationUtils.findAnnotation(clazz, RaptorMessage.class);
        return annotation != null;
    }

    @Override
    protected Object readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

        MediaType contentType = inputMessage.getHeaders().getContentType();
        if (contentType == null) {
            contentType = PROTOBUF;
        }
        Charset charset = contentType.getCharset();
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }
        if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            //TODO json序列化

        } else {
            //TODO protobuf序列化

        }
        return null;
    }

}
