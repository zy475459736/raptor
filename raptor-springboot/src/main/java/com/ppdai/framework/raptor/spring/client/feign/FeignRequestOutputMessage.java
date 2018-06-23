package com.ppdai.framework.raptor.spring.client.feign;

import com.ppdai.framework.raptor.spring.utils.HttpHeadersUtils;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author yinzuolong
 */
public class FeignRequestOutputMessage implements HttpOutputMessage {
    private RequestTemplate request;
    private HttpHeaders httpHeaders;

    public FeignRequestOutputMessage(RequestTemplate request) {
        this.request = request;
        this.httpHeaders = HttpHeadersUtils.getHttpHeaders(request.headers());
    }

    @Override
    public OutputStream getBody() throws IOException {
        return new ByteArrayOutputStream() {
            @Override
            public void flush() throws IOException {
                super.flush();
                request.body(this.toByteArray(), StandardCharsets.UTF_8);
            }
        };
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.httpHeaders;
    }

}