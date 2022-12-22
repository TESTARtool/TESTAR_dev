package org.fruit.monkey.sonarqube.api.request;

import okhttp3.mockwebserver.RecordedRequest;
import org.fruit.monkey.ApiCallTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class SonarqubeIssuesRequestTest extends ApiCallTest {

    @Test
    public void shouldReturnSonarqubeIssuesRequest() throws InterruptedException, IOException {
        mockResponse("sonarqube-api/issues/sonarqube-issues-response.json");

        var sonarqubeIssuesRequest = new SonarqubeIssuesRequest(host, "test-token", "123");
        var parsedResponse = sonarqubeIssuesRequest.send();

        RecordedRequest request = captureRequest();

        assertNotNull(request);
        assertEquals("Basic dGVzdC10b2tlbjo=", request.getHeader("Authorization"));
        assertEquals("/api/issues/search?componentKeys=123&p=1&ps=100", request.getPath());

        var paging = parsedResponse.getPaging();
        assertEquals(1, paging.getPageIndex());
        assertEquals(1, paging.getPageSize());
        assertEquals(1, paging.getTotal());
        assertEquals(1, parsedResponse.getIssues().size());

        var issueFound = parsedResponse.getIssues().get(0);
        assertEquals(issueFound.getKey(), "AYURC_GPHq_OHAqW8iRJ");
        assertEquals(issueFound.getRule(), "java:S110");
        assertEquals(issueFound.getSeverity(), "MAJOR");
        assertEquals(issueFound.getComponent(),
                     "com.marviq.yoho:yoho-be-api:src/main/java/com/marviq/yoho/app/service/factory/exception/FactoryNotFoundException.java");
        assertEquals(issueFound.getLine().longValue(), 8L);
        assertEquals(issueFound.getMessage(), "This class has 10 parents which is greater than 5 authorized.");
        assertEquals(issueFound.getType(), "CODE_SMELL");
    }

}