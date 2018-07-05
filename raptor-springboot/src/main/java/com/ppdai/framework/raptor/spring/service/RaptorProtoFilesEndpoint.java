package com.ppdai.framework.raptor.spring.service;

import com.ppdai.framework.raptor.spring.utils.ProtoFileResourceUtils;
import com.ppdai.framework.raptor.spring.utils.RaptorInterfaceUtils;
import lombok.Setter;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yinzuolong
 */
public class RaptorProtoFilesEndpoint extends AbstractEndpoint {

    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Setter
    private ClassLoader classLoader;

    public RaptorProtoFilesEndpoint(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        super("RaptorProtoFiles", false);
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @Override
    public Object invoke() {
        Map<String, ProtoFileResourceUtils.ProtoFileInfo> protoFileInfoMap = new HashMap<>();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        handlerMethods.forEach((requestMappingInfo, handlerMethod) -> {
            Class<?> raptorInterface = RaptorInterfaceUtils.getInterfaceClass(handlerMethod.getBeanType(), handlerMethod.getMethod());
            if (raptorInterface != null) {
                ProtoFileResourceUtils.ProtoFileInfo protoFileInfo = ProtoFileResourceUtils.findInterfaceProtoFile(raptorInterface, classLoader);
                if (protoFileInfo != null) {
                    protoFileInfoMap.putIfAbsent(protoFileInfo.getFullName(), protoFileInfo);
                }
            }
            MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
            List<Class<?>> messageClasses = Arrays.stream(methodParameters).map(MethodParameter::getParameterType).collect(Collectors.toList());
            MethodParameter methodReturnType = handlerMethod.getReturnType();
            messageClasses.add(methodReturnType.getParameterType());

            List<ProtoFileResourceUtils.ProtoFileInfo> messageProtoFiles = ProtoFileResourceUtils.findProtoFilesByMessage(messageClasses, classLoader);
            messageProtoFiles.forEach(protoFileInfo -> protoFileInfoMap.putIfAbsent(protoFileInfo.getFullName(), protoFileInfo));
        });
        return protoFileInfoMap;
    }
}
