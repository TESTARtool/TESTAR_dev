package org.fruit.monkey.btrace;

import org.fruit.monkey.ApiCallTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BtraceStartRecordingRequestTest extends ApiCallTest {

    @Test
    public void recordingStartSuccessfully() throws IOException {
        mockResponse("btrace-api/recording-started.json");

        var btraceRequest = new BtraceStartRecordingRequest(host);
        var btraceResponse = btraceRequest.send();

        assertNotNull(btraceResponse);
        assertEquals("STARTED", btraceResponse.getStatus());
    }

    @Test
    public void recordingFailedToStart() throws IOException {
        mockResponse("btrace-api/recording-failed-to-start.json");

        var btraceRequest = new BtraceStartRecordingRequest(host);
        var btraceResponse = btraceRequest.send();

        assertNotNull(btraceResponse);
        assertEquals("FAILED_TO_START", btraceResponse.getStatus());
    }

}