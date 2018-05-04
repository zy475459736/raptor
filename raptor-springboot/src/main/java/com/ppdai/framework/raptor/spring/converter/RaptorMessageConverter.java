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

    private final Gson gson = new GsonBuilder()
//            .registerTypeAdapterFactory(new RaptorTypeAdapterFactory())
            .disableHtmlEscaping()
            .create();

    public RaptorMessageConverter() {
        super(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (o == null) {
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
            gson.toJson(o, new OutputStreamWriter(outputMessage.getBody(), charset));
        } else {
            ProtoAdapter protoAdapter = ProtoAdapter.get(o.getClass());
            protoAdapter.encode(outputMessage.getBody(), o);
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
