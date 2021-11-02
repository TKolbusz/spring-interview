package com.example.gostocksum.config;

import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.endpoint.DefaultPasswordTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import org.apache.http.client.HttpClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class OAuthClientConfiguration {

    // Create the Okta client registration
    @Bean
    ClientRegistration oktaClientRegistration() {
        return ClientRegistration
                .withRegistrationId("newdemostock")
                .tokenUri("https://newdemostock.gopos.pl/oauth/token")
                .clientId("0baf8255-97d9-4e0f-b2a3-df25a97bd216")
                .clientSecret("5742a501-8a8f-42c4-b1bd-9cbaa2a84079")
                .scope("main")
                .authorizationGrantType(new AuthorizationGrantType("password"))
                .build();
    }

    // Create the client registration repository
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(ClientRegistration oktaClientRegistration) {
        return new InMemoryClientRegistrationRepository(oktaClientRegistration);
    }

    // Create the authorized client service
    @Bean
    public OAuth2AuthorizedClientService auth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public HttpClient httpClient(){
        return HttpClients.createDefault();}

    // Create the authorized client manager and service manager using the
    // beans created and configured above
    @Bean
    public OAuth2AuthorizedClientManager passwordFlowAuthorizedClientManager(
            HttpClient httpClient,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        DefaultPasswordTokenResponseClient c = new DefaultPasswordTokenResponseClient();
        RestTemplate client = new RestTemplate(requestFactory);
        client.setMessageConverters(Arrays.asList(
                new FormHttpMessageConverter(),
                new OAuth2AccessTokenResponseHttpMessageConverter()));
        client.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        c.setRestOperations(client);

        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .password(configurer -> configurer.accessTokenResponseClient(c))
                .refreshToken()
                .build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        authorizedClientManager.setContextAttributesMapper(authorizeRequest -> {
            Map<String, Object> contextAttributes = new HashMap<>();
            String username = authorizeRequest.getAttribute(OAuth2ParameterNames.USERNAME);
            String password = authorizeRequest.getAttribute(OAuth2ParameterNames.PASSWORD);
            contextAttributes.put(OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME, username);
            contextAttributes.put(OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME, password);
            return contextAttributes;
        });

        return authorizedClientManager;
    }


}