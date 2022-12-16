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
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


public class SonarqubeIssuesRequestTest {
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
                                               .getResourceAsStream("sonarqubeapi/sonarqube-issues-response.json");

        response.setBody(IOUtils.toString(inputStream, StandardCharsets.UTF_8));

        mockWebServer.enqueue(response);

        var sonarqubeIssuesRequest = new SonarqubeIssuesRequest("test-token",
                                                                "http://localhost:8889",
                                                                "123");
        var parsedResponse = sonarqubeIssuesRequest.send();

        RecordedRequest request = mockWebServer.takeRequest(1, TimeUnit.SECONDS);

        assertNotNull(request);
        assertEquals("Basic dGVzdC10b2tlbjo=", request.getHeader("Authorization"));
        assertEquals("/api/issues/search?componentKeys=123", request.getPath());
        var paging = parsedResponse.getPaging();
        assertEquals(1, paging.getPageIndex());
        assertEquals(100, paging.getPageSize());
        assertEquals(385, paging.getTotal());
        assertEquals(1, parsedResponse.getIssues().size());

        var issueFound = parsedResponse.getIssues().get(0);
        assertEquals(issueFound.getKey(),"AYURC_GPHq_OHAqW8iRJ");
        assertEquals(issueFound.getRule(),"java:S110");
        assertEquals(issueFound.getSeverity(),"MAJOR");
        assertEquals(issueFound.getComponent(),"com.marviq.yoho:yoho-be-api:src/main/java/com/marviq/yoho/app/service/factory/exception/FactoryNotFoundException.java");
        assertEquals(issueFound.getLine().longValue(), 8L);
        assertEquals(issueFound.getMessage(), "This class has 10 parents which is greater than 5 authorized.");
        assertEquals(issueFound.getType(),"CODE_SMELL");
    }

}