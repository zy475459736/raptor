package com.ppdai.framework.raptor.spring.service;

import com.ppdai.framework.raptor.spring.utils.ProtoFileResourceUtils;
import com.ppdai.framework.raptor.spring.utils.RaptorInterfaceUtils;
import lombok.Setter;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        Set<Class<?>> raptorInterfaces = new HashSet<>();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        handlerMethods.forEach((requestMappingInfo, handlerMethod) -> {
            Class<?> raptorInterface = RaptorInterfaceUtils.getInterfaceClass(handlerMethod.getBeanType(), handlerMethod.getMethod());
            if (raptorInterface != null && !raptorInterfaces.contains(raptorInterface)) {
                raptorInterfaces.add(raptorInterface);
                ProtoFileResourceUtils.ProtoFileInfo protoFileInfo = ProtoFileResourceUtils.findProtoFile(raptorInterface, classLoader);
                if (protoFileInfo != null) {
                    protoFileInfoMap.putIfAbsent(protoFileInfo.getFullName(), protoFileInfo);
                }
            }
        });
        return protoFileInfoMap;
    }
}
