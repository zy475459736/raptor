package com.ppdai.framework.raptor.spring.client;

import com.ppdai.framework.raptor.annotation.RaptorInterface;
import com.ppdai.framework.raptor.rpc.URL;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author yinzuolong
 */
public interface RaptorClientFactory {

    <T> T create(Class<T> type, URL url);

    boolean support(Class<?> type, URL url);

    abstract class BaseFactory implements RaptorClientFactory {

        protected String getLibrary(Class<?> type) {
            RaptorInterface raptorInterface = AnnotationUtils.findAnnotation(type, RaptorInterface.class);
            if (raptorInterface != null) {
                return raptorInterface.library();
            }
            return null;
        }
    }
}
