package org.fruit.monkey.sonarqube.api;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class SonarqubeApiTest {
    protected MockWebServer mockWebServer;

    protected final int port = 8889;

    protected final String host = "http://localhost:8889";

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(port);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    protected void mockResponse(String jsonFile) throws IOException {
        var response = new MockResponse();
        var inputStream = getClass().getClassLoader()
                .getResourceAsStream(jsonFile);

        response.setBody(IOUtils.toString(inputStream, StandardCharsets.UTF_8));

        mockWebServer.enqueue(response);
    }
}
