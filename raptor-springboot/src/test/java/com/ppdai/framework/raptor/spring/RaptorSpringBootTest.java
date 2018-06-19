package com.ppdai.framework.raptor.spring;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.util.SocketUtils;

/**
 * @author yinzuolong
 */
public class RaptorSpringBootTest {

    public final static int port = SocketUtils.findAvailableTcpPort();

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("server.port", String.valueOf(port));
    }

    @AfterClass
    public static void afterClass() {
        System.clearProperty("server.port");
    }

}
