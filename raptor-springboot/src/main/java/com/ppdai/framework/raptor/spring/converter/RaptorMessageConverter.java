package com.ppdai.framework.raptor.spring.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * @author yinzuolong
 */
public class RaptorMessageConverter extends MappingJackson2HttpMessageConverter {

    public RaptorMessageConverter() {
        super();
    }

    public RaptorMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);
    }
}
