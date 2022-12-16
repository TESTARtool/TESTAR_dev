package org.fruit.monkey.sonarqube.api;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SonarqubeProjectRequestTest {
    public MockWebServer mockWebServer;

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8889);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void shouldReturnSonarqubeIssuesRequest() throws InterruptedException, IOException {
        var response = new MockResponse();
        var inputStream = getClass().getClassLoader()
                                               .getResourceAsStream("sonarqubeapi/sonarqube-projects-response.json");

        response.setBody(IOUtils.toString(inputStream, StandardCharsets.UTF_8));

        mockWebServer.enqueue(response);

        var sonarqubeIssuesRequest = new SonarqubeProjectRequest("test-token", "http://localhost:8889");
        var parsedResponse = sonarqubeIssuesRequest.send();

        RecordedRequest request = mockWebServer.takeRequest(1, TimeUnit.SECONDS);

        assertNotNull(request);
        assertEquals("Basic dGVzdC10b2tlbjo=", request.getHeader("Authorization"));
        assertEquals("/api/projects/search?p=1", request.getPath());

        var paging = parsedResponse.getPaging();

        assertEquals(1, paging.getPageIndex());
        assertEquals(100, paging.getPageSize());
        assertEquals(1, paging.getTotal());
        assertEquals(1, parsedResponse.getComponents().size());

        var projectFound = parsedResponse.getComponents().get(0);
        assertEquals("com.marviq.yoho:yoho-be-api", projectFound.getKey());
        assertEquals(LocalDateTime.of(2022, 12, 14, 15, 20, 53),
                     projectFound.getLastAnalysisDate());
    }
}