package com.ppdai.framework.raptor.spring.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ppdai.framework.raptor.annotation.RaptorMessage;
import com.squareup.wire.ProtoAdapter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * @author yinzuolong
 */
public class RaptorMessageConverter extends AbstractHttpMessageConverter<Object> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final MediaType PROTOBUF = new MediaType("application", "x-protobuf", DEFAULT_CHARSET);

    //TODO 增加json序列化器
    private final Gson gson = new GsonBuilder()
//            .registerTypeAdapterFactory(new RaptorTypeAdapterFactory())
            .disableHtmlEscaping()
            .create();

    public RaptorMessageConverter() {
        super(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (obj == null) {
            return;
        }
        MediaType contentType = outputMessage.getHeaders().getContentType();
        if (contentType == null) {
            contentType = PROTOBUF;
        }
        Charset charset = contentType.getCharset();
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }

        if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody(), charset);
            gson.toJson(obj, writer);
            writer.flush();
        } else {
            ProtoAdapter protoAdapter = ProtoAdapter.get(obj.getClass());
            protoAdapter.encode(outputMessage.getBody(), obj);
        }
    }


    @Override
    protected boolean supports(Class<?> clazz) {
        RaptorMessage annotation = AnnotationUtils.findAnnotation(clazz, RaptorMessage.class);
        return annotation != null;
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

        MediaType contentType = inputMessage.getHeaders().getContentType();
        if (contentType == null) {
            contentType = PROTOBUF;
        }
        Charset charset = contentType.getCharset();
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }
        if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            return gson.fromJson(new InputStreamReader(inputMessage.getBody(), charset), clazz);
        } else {
            ProtoAdapter protoAdapter = ProtoAdapter.get(clazz);
            return protoAdapter.decode(inputMessage.getBody());
        }
    }

}
