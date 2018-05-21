package com.ppdai.framework.raptor.client;

import lombok.Getter;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author yinzuolong
 */
@Getter
public class ApacheHttpClientBuilder {

    private boolean sslHostnameValidationEnabled;
    private int connectTimeout = -1;
    private int socketTimeout = -1;
    private int connectionRequestTimeout = -1;
    private int retryCount = 0;
    private boolean requestSentRetryEnabled = false;
    private int poolMaxTotal = 500;
    private int poolMaxPreRoute = 100;

    private PoolingHttpClientConnectionManager connectionManager;
    private CloseableHttpClient httpClient;

    private final Timer connectionManagerTimer = new Timer("ApacheHttpClientBuilder.connectionManagerTimer", true);

    public static ApacheHttpClientBuilder create() {
        return new ApacheHttpClientBuilder();
    }

    private ApacheHttpClientBuilder() {

    }

    public void build() {
        this.httpClient = newClient();
        this.connectionManagerTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (ApacheHttpClientBuilder.this.connectionManager == null) {
                    return;
                }
                ApacheHttpClientBuilder.this.connectionManager.closeExpiredConnections();
            }
        }, 30000, 5000);
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public ApacheHttpClientBuilder connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public ApacheHttpClientBuilder socketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    public ApacheHttpClientBuilder connectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
        return this;
    }

    public ApacheHttpClientBuilder retryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    public ApacheHttpClientBuilder requestSentRetryEnabled(boolean requestSentRetryEnabled) {
        this.requestSentRetryEnabled = requestSentRetryEnabled;
        return this;
    }


    public ApacheHttpClientBuilder poolMaxPreRoute(int poolMaxPreRoute) {
        this.poolMaxPreRoute = poolMaxPreRoute;
        return this;
    }

    public ApacheHttpClientBuilder poolMaxTotal(int poolMaxTotal) {
        this.poolMaxTotal = poolMaxTotal;
        return this;
    }

    public ApacheHttpClientBuilder sslHostnameValidationEnabled(boolean sslHostnameValidationEnabled) {
        this.sslHostnameValidationEnabled = sslHostnameValidationEnabled;
        return this;
    }

    protected PoolingHttpClientConnectionManager newConnectionManager() {
        try {
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates,
                                               String s) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates,
                                               String s) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, new SecureRandom());

            RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE);
            if (this.sslHostnameValidationEnabled) {
                registryBuilder.register("https",
                        new SSLConnectionSocketFactory(sslContext));
            } else {
                registryBuilder.register("https", new SSLConnectionSocketFactory(
                        sslContext, NoopHostnameVerifier.INSTANCE));
            }
            final Registry<ConnectionSocketFactory> registry = registryBuilder.build();

            this.connectionManager = new PoolingHttpClientConnectionManager(registry);
            this.connectionManager.setMaxTotal(this.poolMaxTotal);
            this.connectionManager.setDefaultMaxPerRoute(this.poolMaxPreRoute);
            return this.connectionManager;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected CloseableHttpClient newClient() {
        final RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(this.socketTimeout)
                .setConnectTimeout(this.connectTimeout)
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .setConnectionRequestTimeout(this.connectionRequestTimeout)
                .build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if (!this.sslHostnameValidationEnabled) {
            httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        }
        return httpClientBuilder.setConnectionManager(newConnectionManager())
                .useSystemProperties()
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(this.retryCount, this.requestSentRetryEnabled))
                .setRedirectStrategy(new RedirectStrategy() {
                    @Override
                    public boolean isRedirected(HttpRequest request,
                                                HttpResponse response, HttpContext context)
                            throws ProtocolException {
                        return false;
                    }

                    @Override
                    public HttpUriRequest getRedirect(HttpRequest request,
                                                      HttpResponse response, HttpContext context)
                            throws ProtocolException {
                        return null;
                    }
                }).build();
    }
}
