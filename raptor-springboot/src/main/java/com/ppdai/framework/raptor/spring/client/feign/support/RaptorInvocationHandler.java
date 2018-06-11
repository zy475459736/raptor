package com.ppdai.framework.raptor.spring.client.feign.support;

import feign.InvocationHandlerFactory;
import feign.Target;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static feign.Util.checkNotNull;

/**
 * @author yinzuolong
 */
@Slf4j
public class RaptorInvocationHandler implements InvocationHandler {

    @Getter
    @Setter
    private List<InvocationInterceptor> invocationInterceptors = new LinkedList<>();
    private Target<?> target;
    private Map<Method, InvocationHandlerFactory.MethodHandler> dispatch;

    public RaptorInvocationHandler(Target target, Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
        this.target = checkNotNull(target, "target");
        this.dispatch = checkNotNull(dispatch, "dispatch for %s", target);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("equals".equals(method.getName())) {
            try {
                Object otherHandler = args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                return equals(otherHandler);
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else if ("hashCode".equals(method.getName())) {
            return hashCode();
        } else if ("toString".equals(method.getName())) {
            return toString();
        }
        Object result = null;
        Exception ex = null;
        try {
            List<String> preHandleResult = new LinkedList<>();
            if (!applyPreHandle(method, args, preHandleResult)) {
                throw new RuntimeException(StringUtils.collectionToDelimitedString(preHandleResult, "\n"));
            }
            result = dispatch.get(method).invoke(args);
            applyPostHandle(method, args, result);
            return result;
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            triggerAfterCompletion(method, args, result, ex);
        }
    }

    protected boolean applyPreHandle(Method method, Object[] args, List<String> preHandleResult) throws Exception {
        if (invocationInterceptors != null) {
            for (InvocationInterceptor interceptor : invocationInterceptors) {
                if (!interceptor.preHandle(method, args, preHandleResult)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void applyPostHandle(Method method, Object[] args, Object result) throws Exception {
        if (invocationInterceptors != null) {
            for (InvocationInterceptor interceptor : invocationInterceptors) {
                interceptor.postHandle(method, args, result);
            }
        }
    }

    protected void triggerAfterCompletion(Method method, Object[] args, Object result, Exception ex) {
        if (invocationInterceptors != null) {
            for (InvocationInterceptor interceptor : invocationInterceptors) {
                try {
                    interceptor.afterCompletion(method, args, result, ex);
                } catch (Exception e) {
                    log.error("RaptorInvocationHandler afterCompletion threw exception", e);
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RaptorInvocationHandler) {
            RaptorInvocationHandler other = (RaptorInvocationHandler) obj;
            return target.equals(other.target);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }

    @Override
    public String toString() {
        return target.toString();
    }
}
