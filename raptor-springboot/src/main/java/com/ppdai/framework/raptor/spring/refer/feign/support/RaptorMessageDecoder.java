package com.ppdai.framework.raptor.spring.refer.feign.support;

import com.ppdai.framework.raptor.annotation.RaptorMessage;
import com.ppdai.framework.raptor.spring.converter.RaptorMessageConverter;
import feign.FeignException;
import feign.Response;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author yinzuolong
 */
public class RaptorMessageDecoder extends SpringDecoder {
    private RaptorMessageConverter raptorMessageConverter;

    public RaptorMessageDecoder(ObjectFactory<HttpMessageConverters> messageConverters, RaptorMessageConverter raptorMessageConverter) {
        super(messageConverters);
        this.raptorMessageConverter = raptorMessageConverter;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        if (type instanceof Class && AnnotationUtils.findAnnotation((Class) type, RaptorMessage.class) != null) {
            return raptorMessageConverter.read((Class) type, new SpringDecoder.FeignResponseAdapter(response));
        }
        return super.decode(response, type);
    }
}
