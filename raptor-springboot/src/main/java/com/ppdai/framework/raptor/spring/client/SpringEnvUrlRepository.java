package com.ppdai.framework.raptor.spring.client;

import com.ppdai.framework.raptor.annotation.RaptorInterface;
import com.ppdai.framework.raptor.rpc.URL;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

public class SpringEnvUrlRepository {

    private Environment environment;

    private static final String URL_PREFIX = "raptor.url.";
    private static final String APP_URL_PREFIX = "raptor.app-url.";

    public SpringEnvUrlRepository(Environment environment) {
        this.environment = environment;
    }

    public URL getUrl(Class<?> interfaceClass, String url) {
        if (StringUtils.isEmpty(url)) {
            url = environment.getProperty(URL_PREFIX + interfaceClass.getName());
        }
        if (StringUtils.isEmpty(url)) {
            RaptorInterface raptorInterface = AnnotationUtils.findAnnotation(interfaceClass, RaptorInterface.class);
            String appName = raptorInterface.appName();
            String appId = raptorInterface.appId();

            if (StringUtils.isEmpty(url) && !StringUtils.isEmpty(appName)) {
                url = environment.getProperty(APP_URL_PREFIX + appName);
            }
            if (StringUtils.isEmpty(url) && !StringUtils.isEmpty(appId)) {
                url = environment.getProperty(APP_URL_PREFIX + appId);
            }
            if (StringUtils.isEmpty(url) && !StringUtils.isEmpty(appName)) {
                url = "http://" + appName;
            }
        }
        return StringUtils.isEmpty(url) ? null : URL.valueOf(url);
    }
}
