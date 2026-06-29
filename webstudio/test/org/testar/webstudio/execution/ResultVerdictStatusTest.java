package org.testar.webstudio.execution;

import org.junit.Assert;
import org.junit.Test;

public class ResultVerdictStatusTest {

    @Test
    public void treatsOkAndLlmCompleteAsSuccessfulResults() {
        Assert.assertEquals("ok", ResultVerdictStatus.forResultFileName("sequence_1_V001_OK.html"));
        Assert.assertEquals("ok", ResultVerdictStatus.forResultFileName("sequence_1_V001_LLM_COMPLETE.html"));
    }

    @Test
    public void treatsLlmInvalidAndOtherVerdictsAsFailedResults() {
        Assert.assertEquals("failed", ResultVerdictStatus.forResultFileName("sequence_1_V001_LLM_INVALID.html"));
        Assert.assertEquals("failed", ResultVerdictStatus.forResultFileName("sequence_1_V001_SUSPICIOUS_TAG.html"));
    }

    @Test
    public void treatsFilesWithoutVerdictAsSuccessfulMetadata() {
        Assert.assertEquals("ok", ResultVerdictStatus.forResultFileName("index.html"));
    }
}
