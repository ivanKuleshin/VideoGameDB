package com.ai.tester.config;

import com.ai.tester.client.http.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig implements ApplicationListener<WebServerInitializedEvent> {

    @Value("${http.client.base-url}")
    private String baseUrl;

    @Value("${http.client.base-path}")
    private String basePath;

    @Value("${http.client.username}")
    private String username;

    @Value("${http.client.password}")
    private String password;

    @Bean
    public HttpClient httpClient() {
        return HttpClient.getInstance();
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        HttpClient.getInstance().init(baseUrl, port, basePath, username, password);
    }
}
