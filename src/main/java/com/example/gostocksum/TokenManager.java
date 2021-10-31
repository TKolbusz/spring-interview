package com.example.gostocksum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.endpoint.OAuth2PasswordGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

@Component
public class TokenManager {

    @Value("${client.registration.name}")
    private String clientRegistrationName;

    @Value("${spring.security.oauth2.client.registration.newdemostock.client-id}")
    private String clientId;

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public TokenManager(
        OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    public String getAccessToken() {
        OAuth2AuthorizeRequest authorizeRequest =
            OAuth2AuthorizeRequest
                .withClientRegistrationId(clientRegistrationName)
                .principal(clientId)
                .build();

        OAuth2AuthorizedClient authorizedClient =
            this.authorizedClientManager
                .authorize(authorizeRequest);

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        return accessToken.getTokenValue();
    }
}
