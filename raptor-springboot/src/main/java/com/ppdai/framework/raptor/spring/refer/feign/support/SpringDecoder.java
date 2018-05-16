
package com.ppdai.framework.raptor.spring.refer.feign.support;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpMessageConverterExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;


public class SpringDecoder implements Decoder {

    private ObjectFactory<HttpMessageConverters> messageConverters;

    public SpringDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Override
    public Object decode(final Response response, Type type)
            throws IOException, FeignException {
        if (type instanceof Class || type instanceof ParameterizedType
                || type instanceof WildcardType) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            HttpMessageConverterExtractor<?> extractor = new HttpMessageConverterExtractor(
                    type, this.messageConverters.getObject().getConverters());

            return extractor.extractData(new FeignResponseAdapter(response));
        }
        throw new DecodeException(
                "type is not an instance of Class or ParameterizedType: " + type);
    }

    public class FeignResponseAdapter implements ClientHttpResponse {

        private final Response response;

        public FeignResponseAdapter(Response response) {
            this.response = response;
        }

        @Override
        public HttpStatus getStatusCode() throws IOException {
            return HttpStatus.valueOf(this.response.status());
        }

        @Override
        public int getRawStatusCode() throws IOException {
            return this.response.status();
        }

        @Override
        public String getStatusText() throws IOException {
            return this.response.reason();
        }

        @Override
        public void close() {
            try {
                this.response.body().close();
            } catch (IOException ex) {
                // Ignore exception on close...
            }
        }

        @Override
        public InputStream getBody() throws IOException {
            return this.response.body().asInputStream();
        }

        @Override
        public HttpHeaders getHeaders() {
            return FeignUtils.getHttpHeaders(this.response.headers());
        }

    }

}
