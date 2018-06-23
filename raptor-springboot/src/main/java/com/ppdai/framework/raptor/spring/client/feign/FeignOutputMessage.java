package com.ppdai.framework.raptor.spring.client.feign;

import com.ppdai.framework.raptor.spring.utils.HttpHeadersUtils;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author yinzuolong
 */
public class FeignOutputMessage implements HttpOutputMessage {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private final HttpHeaders httpHeaders;

    public FeignOutputMessage(RequestTemplate request) {
        httpHeaders = HttpHeadersUtils.getHttpHeaders(request.headers());
    }

    @Override
    public OutputStream getBody() throws IOException {
        return this.outputStream;
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.httpHeaders;
    }

    public ByteArrayOutputStream getOutputStream() {
        return this.outputStream;
    }

}