package com.ppdai.framework.raptor.spring.service;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Transaction;
import com.ppdai.framework.raptor.common.ParamNameConstants;
import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.spring.utils.PropertyContext;
import com.ppdai.framework.raptor.spring.utils.RaptorClassUtils;
import com.ppdai.framework.raptor.util.ReflectUtil;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

/**
 * @author yinzuolong
 */
public class CatInterceptor extends HandlerInterceptorAdapter {

    private static final String CAT_ATTRIBUTE = "raptor-cat-transaction";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String name = "service";
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String method = ReflectUtil.getMethodSignature(handlerMethod.getMethod());
            String type = RaptorClassUtils.getInterfaceName(ClassUtils.getUserClass(handlerMethod.getBean()), handlerMethod.getMethod());
            name = type + "#" + method;
        }
        catTrace(request);
        Transaction transaction = Cat.newTransaction(getTransactionType(), name);
        RequestContextHolder.getRequestAttributes().setAttribute(CAT_ATTRIBUTE, transaction, SCOPE_REQUEST);
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {

        Transaction transaction = (Transaction) RequestContextHolder.getRequestAttributes().getAttribute(CAT_ATTRIBUTE, SCOPE_REQUEST);
        if (transaction != null) {
            if (ex == null) {
                transaction.setStatus(Transaction.SUCCESS);
            } else {
                Cat.logError(ex);
                transaction.setStatus(ex);
            }
        }
        super.afterCompletion(request, response, handler, ex);
    }


    protected void catTrace(HttpServletRequest request) {
        Cat.logEvent("service.app", Cat.getManager().getDomain());
        PropertyContext propertyContext = new PropertyContext();
        propertyContext.addProperty(Cat.Context.ROOT, request.getHeader(CatConstants.HTTP_HEADER_ROOT_MESSAGE_ID));
        propertyContext.addProperty(Cat.Context.PARENT, request.getHeader(CatConstants.HTTP_HEADER_PARENT_MESSAGE_ID));
        propertyContext.addProperty(Cat.Context.CHILD, request.getHeader(CatConstants.HTTP_HEADER_CHILD_MESSAGE_ID));
        Cat.logRemoteCallServer(propertyContext);
        Cat.logRemoteCallServer(propertyContext);
        String clientApp = request.getHeader(PropertyContext.APP_NAME);
        Cat.logEvent("client.app", clientApp == null ? "unknown" : clientApp);
        Cat.logEvent("requestId", String.valueOf(request.getHeader(ParamNameConstants.REQUEST_ID)));
    }

    protected String getTransactionType() {
        return RaptorConstants.METRIC_NAME + "." + RaptorConstants.NODE_TYPE_SERVICE;
    }
}
