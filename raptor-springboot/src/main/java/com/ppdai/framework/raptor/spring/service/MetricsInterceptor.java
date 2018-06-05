package com.ppdai.framework.raptor.spring.service;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.ppdai.framework.raptor.common.RaptorInfo;
import com.ppdai.framework.raptor.metric.MetricContext;
import com.ppdai.framework.raptor.metric.TagName;
import com.ppdai.framework.raptor.spring.utils.RaptorClassUtils;
import com.ppdai.framework.raptor.util.ReflectUtil;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.ppdai.framework.raptor.common.ParamNameConstants.HOST_CLIENT;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

/**
 * @author yinzuolong
 */
public class MetricsInterceptor extends HandlerInterceptorAdapter {
    public static final String REQUEST_START_TIME = "raptor-RequestStartTime";
    public static final String TIME_NAME = RaptorInfo.getInstance().getMetricPrefix() + ".time";
    public static final String COUNT_NAME = RaptorInfo.getInstance().getMetricPrefix() + ".count";

    protected MetricRegistry metricRegistry = MetricContext.getMetricRegistry();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        RequestContextHolder.getRequestAttributes().setAttribute(REQUEST_START_TIME, System.nanoTime(), SCOPE_REQUEST);
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {

        Map<String, String> tags = httpRequestTags(request, response, handler, ex);

        metricRegistry.counter(new TagName(COUNT_NAME, tags).toString()).inc();

        Long startTime = (Long) RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_START_TIME, SCOPE_REQUEST);
        if (startTime != null) {
            Timer timer = metricRegistry.timer(new TagName(TIME_NAME, tags).toString());
            timer.update(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        }
        super.afterCompletion(request, response, handler, ex);
    }


    public Map<String, String> httpRequestTags(HttpServletRequest request, HttpServletResponse response,
                                               Object handler, Exception ex) {
        Map<String, String> tags = new HashMap<>();
        tags.put("raptorVersion", RaptorInfo.getInstance().getVersion());
        tags.put("nodeType", "service");

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String method = ReflectUtil.getMethodSignature(handlerMethod.getMethod());
            String type = RaptorClassUtils.getInterfaceName(ClassUtils.getUserClass(handlerMethod.getBean()), handlerMethod.getMethod());
            tags.put("method", method);
            tags.put("interface", type);
        }

        String clientHost = request.getHeader(HOST_CLIENT);
        if (!StringUtils.isEmpty(clientHost)) {
            tags.put("clientHost", clientHost);
        }

        if (ex != null) {
            tags.put("exception", ex.getClass().getSimpleName());
        }

        tags.put("status", ((Integer) response.getStatus()).toString());

        return tags;
    }

}
