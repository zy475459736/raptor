package com.ppdai.framework.raptor.spring.client;

import com.ppdai.framework.raptor.annotation.RaptorInterface;
import com.ppdai.framework.raptor.refer.ReferProxyBuilder;
import com.ppdai.framework.raptor.rpc.URL;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

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

    class Default extends BaseFactory {

        private static final String support = "raptor";
        private ReferProxyBuilder referProxyBuilder;

        public Default(ReferProxyBuilder referProxyBuilder) {
            this.referProxyBuilder = referProxyBuilder;
        }

        @Override
        public <T> T create(Class<T> type, URL url) {
            return referProxyBuilder.build(type, url);
        }

        @Override
        public boolean support(Class<?> type, URL url) {
            String library = getLibrary(type);
            return StringUtils.isEmpty(library) || support.equalsIgnoreCase(library);
        }

    }
}
