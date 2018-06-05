package com.ppdai.framework.raptor.spring.service;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.ppdai.framework.raptor.metric.MetricContext;
import com.ppdai.framework.raptor.spring.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author yinzuolong
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MetricsTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Environment env;


    @Test
    public void testMetrics() {
        String p = env.getProperty("server.port");
        for (int i = 0; i < 10; i++) {
            String url = "/more?name=ppdai";
            String response = restTemplate.getForObject(url, String.class);
            System.out.println(response);
        }

        MetricRegistry metricRegistry = MetricContext.getMetricRegistry();
        ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).build();
        consoleReporter.report();
    }
}
