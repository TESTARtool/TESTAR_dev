package org.fruit.monkey;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public abstract class ApiCallTest {
    private MockWebServer mockWebServer;

    protected final int port = 8889;

    protected final String host = "http://localhost:8889";

    private MockResponse mockResponse;


    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(port);
        mockResponse = new MockResponse();
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    protected void mockResponse(String jsonFile) throws IOException {
        var inputStream = getClass().getClassLoader()
                .getResourceAsStream(jsonFile);

        mockResponse.setBody(IOUtils.toString(inputStream, StandardCharsets.UTF_8));

        mockWebServer.enqueue(mockResponse);
    }

    protected RecordedRequest captureRequest() throws InterruptedException {
        return mockWebServer.takeRequest(1, TimeUnit.SECONDS);
    }
}
