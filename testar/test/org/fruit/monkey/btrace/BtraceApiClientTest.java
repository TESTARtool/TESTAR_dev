package org.fruit.monkey.btrace;

import org.fruit.monkey.ApiCallTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
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

}