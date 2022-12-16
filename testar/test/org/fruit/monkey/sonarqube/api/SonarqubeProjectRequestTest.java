package org.fruit.monkey.sonarqube.api;

import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SonarqubeProjectRequestTest extends SonarqubeApiTest {

    @Test
    public void shouldReturnSonarqubeIssuesRequest() throws InterruptedException, IOException {
        mockResponse("sonarqubeapi/projects/sonarqube-projects-response.json");

        var sonarqubeIssuesRequest = new SonarqubeProjectRequest(host, "test-token");
        var parsedResponse = sonarqubeIssuesRequest.send();

        RecordedRequest request = captureRequest();

        assertNotNull(request);
        assertEquals("Basic dGVzdC10b2tlbjo=", request.getHeader("Authorization"));
        assertEquals("/api/projects/search?p=1&ps=100", request.getPath());

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