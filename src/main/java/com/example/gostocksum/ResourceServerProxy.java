package com.example.gostocksum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ResourceServerProxy {

    public static final String AUTHORIZATION = "Authorization";

    @Value("${resource.server.url}")
    private String resourceServerURL;

    private final TokenManager tokenManager;

    private final RestTemplate restTemplate;

    public ResourceServerProxy(
        TokenManager tokenManager,
        RestTemplate restTemplate) {

        this.tokenManager = tokenManager;
        this.restTemplate = restTemplate;
    }

    public String callData() {
        String token = tokenManager.getAccessToken();

        String url = resourceServerURL + "/api/249/purchase_orders";

        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        var response =
            restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        return response.getBody();
    }
}
