package com.ppdai.framework.raptor.benchmark.springmvc.okhttp;

import com.ppdai.framework.raptor.benchmark.server.SimpleImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author yinzuolong
 */
@SpringBootApplication
@Configuration
@Import(SimpleImpl.class)
public class OkhttpSpringMvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(OkhttpSpringMvcApplication.class, args);
    }
}
