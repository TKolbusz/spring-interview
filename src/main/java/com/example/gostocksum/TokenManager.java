package com.example.gostocksum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TokenManager {

    @Autowired
    private OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    public String getAccessToken() {
        String username = "zadanie@zadanie.com";
        String password = "zadanie";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ClientRegistration r = clientRegistrationRepository.findByRegistrationId("newdemostock");

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(r.getRegistrationId())
                .principal(authentication)
                .attributes(attrs -> {
                    attrs.put(OAuth2ParameterNames.USERNAME, username);
                    attrs.put(OAuth2ParameterNames.PASSWORD, password);
                })
                .build();
        OAuth2AuthorizedClient authorizedClient = this.oAuth2AuthorizedClientManager.authorize(authorizeRequest);
        // Get the token from the authorized client object
        OAuth2AccessToken accessToken = Objects.requireNonNull(authorizedClient).getAccessToken();


        return accessToken.getTokenValue();
    }
}
