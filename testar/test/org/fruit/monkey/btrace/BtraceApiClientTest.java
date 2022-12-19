package org.fruit.monkey.btrace;

import org.fruit.monkey.ApiCallTest;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BtraceApiClientTest extends ApiCallTest {

    @Test
    public void shouldStartRecording() throws IOException {
        mockResponse("btrace-api/recording-started.json");

        var apiClient = new BtraceApiClient(host);
        var result = apiClient.startRecordingMethodInvocation();

        assertTrue(result);
    }

    @Test
    public void shouldFailToStartRecording() throws IOException {
        mockResponse("btrace-api/recording-failed-to-start.json");

        var apiClient = new BtraceApiClient(host);
        var result = apiClient.startRecordingMethodInvocation();

        assertFalse(result);
    }

    @Test
    public void shouldReturnMethodInvocations() throws IOException {
        mockResponse("btrace-api/recording-finished-duplicate-calls.json");

        var apiClient = new BtraceApiClient(host);
        var result = apiClient.finishRecordingMethodInvocation();

        assertNotNull(result);
        assertEquals(expectedMethodInvocations().size(), result.size());
        assertTrue(result.containsAll(expectedMethodInvocations()));
    }

    private List<MethodInvocation> expectedMethodInvocations() {
        return List.of(new MethodInvocation("com.marviq.yoho.domain.model.media.MediaEntity",
                                            "<init>",
                                            4),
                       new MethodInvocation("com.marviq.yoho.domain.model.user.UserEntity",
                                            "getFactory",
                                            2),
                       new MethodInvocation("com.marviq.yoho.domain.repository.feed.FeedRepositoryQueryDslImpl",
                                            "listFeedsInFactory",
                                            1));


    }

}