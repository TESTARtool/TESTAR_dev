package org.fruit.monkey.sonarqube.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

import java.io.IOException;
import java.util.Base64;

public abstract class SonarqubeRequest<T> {

    private final String username;

    protected final String host;

    private final Class<T> responseType;

    private ObjectMapper objectMapper;

    private static final String SEPARATOR = ":";

    private static final String AUTH_PREFIX = "Basic ";

    private static final String AUTH_KEY = "Authorization";

    public SonarqubeRequest(String username, String host, Class<T> responseType) {
        this.username = username;
        this.host = host;
        this.responseType = responseType;
        configureObjectMapper();
    }

    private void configureObjectMapper() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    protected T send() {
        var httpRequest = new HttpGet(buildUri());
        httpRequest.addHeader(AUTH_KEY, prepareBasicAuth());

        try (var httClient = HttpClientBuilder.create().build();
             var httpResponse = httClient.execute(httpRequest)) {
            System.out.println(httpResponse.getEntity().getContent().toString());
            return objectMapper.readValue(httpResponse.getEntity().getContent(), responseType);
        } catch (IOException e) {
            throw SonarqubeApiException.sonarqubeApiCallResultedInError(e);
        }
    }

    abstract protected String buildUri();


    protected String prepareBasicAuth() {
        return AUTH_PREFIX + encodeCredentials();
    }

    private String encodeCredentials() {
        String sonarqubeCredentials = username + ":";
        return Base64.getEncoder().encodeToString(sonarqubeCredentials.getBytes());
    }
}
