package org.fruit.monkey.btrace;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

import java.io.IOException;

public abstract class BtraceRequest<T extends BtraceResponse> {
    protected final String host;

    private final Class<T> responseType;

    private ObjectMapper objectMapper;

    public BtraceRequest(String host, Class<T> responseType) {
        this.host = host;
        this.responseType = responseType;
        objectMapper = new ObjectMapper();
    }

    protected T send() {
        var httpRequest = new HttpPost(buildUri());

        try (var httClient = HttpClientBuilder.create().build();
             var httpResponse = httClient.execute(httpRequest)) {
            return objectMapper.readValue(httpResponse.getEntity().getContent(), responseType);
        } catch (IOException e) {
            throw BtraceApiException.btraceApiCallResultedInError(e);
        }
    }

    abstract protected String buildUri();

}