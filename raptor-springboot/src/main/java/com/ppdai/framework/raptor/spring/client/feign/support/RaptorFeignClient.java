package com.ppdai.framework.raptor.spring.client.feign.support;

import feign.Client;
import feign.Request;
import feign.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author yinzuolong
 */
@Slf4j
public class RaptorFeignClient implements Client {

    private final Client client;

    @Getter
    @Setter
    private List<ClientInterceptor> clientInterceptors = new ArrayList<>();

    public RaptorFeignClient(Client client) {
        this.client = client;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        Response response = null;
        Exception ex = null;
        List<String> preHandleResult = new LinkedList<>();
        try {
            if (!applyPreHandle(request, options, preHandleResult)) {
                throw new RuntimeException(StringUtils.collectionToDelimitedString(preHandleResult, "\n"));
            }
            response = client.execute(request, options);
            applyPostHandle(request, response);
            return response;
        } catch (Exception e) {
            ex = e;
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new RuntimeException("Request execute error.", ex);
        } finally {
            triggerAfterCompletion(request, response, ex);
        }
    }

    protected boolean applyPreHandle(Request request, Request.Options options, List<String> preHandleResult) throws Exception {
        if (clientInterceptors != null) {
            for (ClientInterceptor interceptor : clientInterceptors) {
                if (!interceptor.preHandle(request, options, preHandleResult)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void applyPostHandle(Request request, Response response) throws Exception {
        if (clientInterceptors != null) {
            for (ClientInterceptor interceptor : clientInterceptors) {
                interceptor.postHandle(request, response);
            }
        }
    }

    protected void triggerAfterCompletion(Request request, Response response, Exception ex) {
        if (clientInterceptors != null) {
            for (ClientInterceptor interceptor : clientInterceptors) {
                try {
                    interceptor.afterCompletion(request, response, ex);
                } catch (Exception e) {
                    log.error("RaptorFeignClient afterCompletion throw exception", e);
                }
            }
        }
    }
}
