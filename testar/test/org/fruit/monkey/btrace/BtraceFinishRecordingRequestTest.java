package org.fruit.monkey.btrace;

import org.fruit.monkey.ApiCallTest;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BtraceFinishRecordingRequestTest extends ApiCallTest {

    @Test
    public void recordingFinishedSuccessfully() throws IOException {
        mockResponse("btrace-api/recording-finished.json");

        var finishRecordingRequest = new BtraceFinishRecordingRequest(host);
        var finishRecordingResponse = finishRecordingRequest.send();

        assertNotNull(finishRecordingResponse);
        assertEquals("FINISHED", finishRecordingResponse.getStatus());
        assertEquals(3, finishRecordingResponse.getMethodsExecuted().size());
        assertTrue(finishRecordingResponse.getMethodsExecuted().containsAll(expectedMethodEntries()));

    }

    private List<BtraceFinishRecordingResponse.MethodEntry> expectedMethodEntries() {
        var methodEntry1 = new BtraceFinishRecordingResponse.MethodEntry();
        methodEntry1.setClassName("com.marviq.yoho.domain.model.media.MediaEntity");
        methodEntry1.setMethodName("<init>");
        methodEntry1.setParameterTypes(List.of("String", "String"));

        var methodEntry2 = new BtraceFinishRecordingResponse.MethodEntry();
        methodEntry2.setClassName("com.marviq.yoho.domain.model.user.UserEntity");
        methodEntry2.setMethodName("getFactory");
        methodEntry2.setParameterTypes(List.of());

        var methodEntry3 = new BtraceFinishRecordingResponse.MethodEntry();
        methodEntry3.setClassName("com.marviq.yoho.domain.repository.feed.FeedRepositoryQueryDslImpl");
        methodEntry3.setMethodName("listFeedsInFactory");
        methodEntry3.setParameterTypes(List.of("FactoryId"));


        return List.of(methodEntry1, methodEntry2, methodEntry3);
    }
}