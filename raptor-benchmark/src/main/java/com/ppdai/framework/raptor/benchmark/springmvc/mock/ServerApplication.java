package com.ppdai.framework.raptor.benchmark.springmvc.mock;

import com.ppdai.framework.raptor.spring.RaptorAutoConfiguration;
import com.ppdai.framework.raptor.spring.client.RaptorClientAutoConfiguration;
import com.ppdai.framework.raptor.spring.service.RaptorServiceAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yinzuolong
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {RaptorAutoConfiguration.class, RaptorClientAutoConfiguration.class, RaptorServiceAutoConfiguration.class})
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
