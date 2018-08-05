package com.ppdai.framework.raptor.spring.autoconfig;

import com.ppdai.framework.raptor.annotation.RaptorInterface;
import com.ppdai.framework.raptor.exception.RaptorServiceException;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.service.Endpoint;
import com.ppdai.framework.raptor.service.Provider;
import com.ppdai.framework.raptor.service.ProviderBuilder;
import com.ppdai.framework.raptor.spring.annotation.RaptorService;
import com.ppdai.framework.raptor.spring.utils.AopHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@Slf4j
public class RaptorServiceProcessor implements BeanPostProcessor {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ProviderBuilder    providerBuilder;

    @Override
    public Object postProcessAfterInitialization(Object springBean, String beanName) throws BeansException {
        Object bean = springBean;
        if (AopUtils.isAopProxy(bean)) {
            try {//todo
                bean = AopHelper.getTarget(bean);
            } catch (Exception e) {
                throw new RaptorServiceException(String.format("Can not find proxy target for %s .", bean.getClass().getName()));
            }
        }
        Annotation annotation = AnnotationUtils.findAnnotation(bean.getClass(), RaptorService.class);
        if (annotation == null) {
            return springBean;
        }
        /**带有RaptorService注解的BEAN执行以下逻辑**/
        List<Class<?>> interfaceClasses = findAllInterfaces(bean);
        if (interfaceClasses.size() == 0) {
            throw new RaptorServiceException(String.format("Can not find %s serviceImpl's interface.", bean.getClass().getName()));
        }
        DefaultListableBeanFactory defaultListableBeanFactory;
        for (Class<?> interfaceClass : interfaceClasses) {
            registryRaptorService(interfaceClass, springBean);
        }
        return springBean;
    }


    /**
     * 注册Bean(契约接口的实现类)到 EndPoint
     * @param interfaceClass 带有@RaptorInterface注解、RPC/Protobuf Service所对应接口
     * @param bean 容器中的Bean实例，实际上是RPC/Protobuf Service所对应接口的实现类
     * */
    @SuppressWarnings("unchecked")
    private void registryRaptorService(Class interfaceClass, Object bean) {
        Provider provider = providerBuilder.build(interfaceClass, bean);
        Map<String, Endpoint> endpointMap = applicationContext.getBeansOfType(Endpoint.class);
        for (Endpoint endpoint : endpointMap.values()) {
            URL url = endpoint.export(provider);
            log.info("register a raptor service '{}' to endpoint '{}'", interfaceClass.getName(), url.getUri());
        }
    }

    /**
     * Find all the interfaces the specific BEAN implements,
     * and retrun those interfaces which is annotated by RapterInterface.
     * 获取Bean的所有接口，然后返回带有RaptorInterface注解的interface class
     * */
    private List<Class<?>> findAllInterfaces(Object bean) {
        List<Class<?>> raptorInterfaces = new ArrayList<>();
        Set<Class<?>> interfaceClasses = ClassUtils.getAllInterfacesAsSet(bean);
        for (Class<?> interfaceClass : interfaceClasses) {
            Annotation annotation = AnnotationUtils.findAnnotation(interfaceClass, RaptorInterface.class);
            if (annotation != null) {
                raptorInterfaces.add(interfaceClass);
            }
        }
        return raptorInterfaces;
    }
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
