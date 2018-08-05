package com.ppdai.framework.raptor.service;

import com.ppdai.framework.raptor.common.ParamNameConstants;
import com.ppdai.framework.raptor.common.RaptorMessageConstant;
import com.ppdai.framework.raptor.exception.RaptorBizException;
import com.ppdai.framework.raptor.exception.RaptorServiceException;
import com.ppdai.framework.raptor.rpc.DefaultResponse;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.util.NetUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class DefaultProvider<T> extends AbstractProvider<T> {

    public DefaultProvider(Class<T> interfaceClass, T serviceInstance) {
        super(interfaceClass, serviceInstance);
    }
    /**
     * 调用于AbstractProvider.call <- ServletEndpoint.doPost
     * */
    @Override
    protected Response invoke(Request request) {
        DefaultResponse response = new DefaultResponse();
        response.setAttachment(ParamNameConstants.HOST_SERVER, NetUtils.getLocalIp());
        Method method = lookupMethod(request.getMethodName(), null);
        if (method == null) {
            RaptorServiceException exception =
                    new RaptorServiceException(RaptorMessageConstant.SERVICE_NOTFOUND_ERROR_CODE,
                            "Service method not exist: "
                                    + request.getInterfaceName()
                                    + "#" + request.getMethodName(),
                            null);
            response.setException(exception);
            return response;
        }

        try {
            /**
             * 反射
             * */
            Object value = method.invoke(this.serviceInstance, request.getArgument());
            response.setValue(value);
        } catch (Throwable e) {
            if (e.getCause() != null) {
                response.setException(e.getCause());
            } else {
                response.setException(new RaptorBizException("provider call process error", e));
            }
            //服务发生错误时，显示详细日志
            log.error("Exception caught when during method invocation. request:" + request.toString(), e);
        }
        return response;
    }

}
