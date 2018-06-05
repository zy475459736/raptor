package com.ppdai.framework.raptor.spring.client.feign.support;

import feign.Client;
import feign.Request;
import feign.Response;
import feign.Util;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static feign.Util.UTF_8;

/**
 * @author yinzuolong
 */
public class RaptorFeignClient implements Client {
    private static final String ACCEPT_HEADER_NAME = "Accept";

    private final HttpClient client;

    public RaptorFeignClient() {
        this(HttpClientBuilder.create().build());
    }

    public RaptorFeignClient(HttpClient client) {
        this.client = client;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        HttpUriRequest httpUriRequest;
        try {
            httpUriRequest = toHttpUriRequest(request, options);
        } catch (URISyntaxException e) {
            throw new IOException("URL '" + request.url() + "' couldn't be parsed into a URI", e);
        }
        HttpResponse httpResponse = client.execute(httpUriRequest);
        return toFeignResponse(httpResponse).toBuilder().request(request).build();
    }

    private HttpUriRequest toHttpUriRequest(Request request, Request.Options options) throws
            UnsupportedEncodingException, MalformedURLException, URISyntaxException {
        RequestBuilder requestBuilder = RequestBuilder.create(request.method());

        //per request timeouts
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectTimeout(options.connectTimeoutMillis())
                .setSocketTimeout(options.readTimeoutMillis())
                .build();
        requestBuilder.setConfig(requestConfig);

        URI uri = new URIBuilder(request.url()).build();

        requestBuilder.setUri(uri.getScheme() + "://" + uri.getAuthority() + uri.getRawPath());

        //request query params
        List<NameValuePair> queryParams = URLEncodedUtils.parse(uri, requestBuilder.getCharset().name());
        for (NameValuePair queryParam : queryParams) {
            requestBuilder.addParameter(queryParam);
        }

        //request headers
        boolean hasAcceptHeader = false;
        for (Map.Entry<String, Collection<String>> headerEntry : request.headers().entrySet()) {
            String headerName = headerEntry.getKey();
            if (headerName.equalsIgnoreCase(ACCEPT_HEADER_NAME)) {
                hasAcceptHeader = true;
            }

            if (headerName.equalsIgnoreCase(Util.CONTENT_LENGTH)) {
                // The 'Content-Length' header is always set by the Apache client and it
                // doesn't like us to set it as well.
                continue;
            }

            for (String headerValue : headerEntry.getValue()) {
                requestBuilder.addHeader(headerName, headerValue);
            }
        }
        //some servers choke on the default accept string, so we'll set it to anything
        if (!hasAcceptHeader) {
            requestBuilder.addHeader(ACCEPT_HEADER_NAME, "*/*");
        }

        //request body
        if (request.body() != null) {
            HttpEntity entity = null;
            if (request.charset() != null) {
                ContentType contentType = getContentType(request);
                String content = new String(request.body(), request.charset());
                entity = new StringEntity(content, contentType);
            } else {
                entity = new ByteArrayEntity(request.body());
            }

            requestBuilder.setEntity(entity);
        }

        return requestBuilder.build();
    }

    private ContentType getContentType(Request request) {
        ContentType contentType = ContentType.DEFAULT_TEXT;
        for (Map.Entry<String, Collection<String>> entry : request.headers().entrySet())
            if (entry.getKey().equalsIgnoreCase("Content-Type")) {
                Collection<String> values = entry.getValue();
                if (values != null && !values.isEmpty()) {
                    contentType = ContentType.parse(values.iterator().next());
                    break;
                }
            }
        return contentType;
    }

    private Response toFeignResponse(HttpResponse httpResponse) throws IOException {
        StatusLine statusLine = httpResponse.getStatusLine();
        int statusCode = statusLine.getStatusCode();

        String reason = statusLine.getReasonPhrase();

        Map<String, Collection<String>> headers = new HashMap<String, Collection<String>>();
        for (Header header : httpResponse.getAllHeaders()) {
            String name = header.getName();
            String value = header.getValue();

            headers.putIfAbsent(name, new ArrayList<>());
            Collection<String> headerValues = headers.get(name);
            headerValues.add(value);
        }

        return Response.builder()
                .status(statusCode)
                .reason(reason)
                .headers(headers)
                .body(toFeignBody(httpResponse))
                .build();
    }

    private Response.Body toFeignBody(HttpResponse httpResponse) throws IOException {
        final HttpEntity entity = httpResponse.getEntity();
        if (entity == null) {
            return null;
        }
        return new Response.Body() {

            @Override
            public Integer length() {
                return entity.getContentLength() >= 0 && entity.getContentLength() <= Integer.MAX_VALUE ?
                        (int) entity.getContentLength() : null;
            }

            @Override
            public boolean isRepeatable() {
                return entity.isRepeatable();
            }

            @Override
            public InputStream asInputStream() throws IOException {
                return entity.getContent();
            }

            @Override
            public Reader asReader() throws IOException {
                return new InputStreamReader(asInputStream(), UTF_8);
            }

            @Override
            public void close() throws IOException {
                EntityUtils.consume(entity);
            }
        };
    }
}
