package org.testar.reporting.sequence.record;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.tag.Tags;
import org.testar.stub.StateStub;

public class TestDefaultSemanticStateExtractor {

    @Test
    public void testExtractNullStateReturnsEmptyRecord() {
        DefaultSemanticStateExtractor extractor = new DefaultSemanticStateExtractor();

        SemanticStateRecord record = extractor.extract(null);

        Assert.assertEquals(Map.of(), record.getProperties());
    }

    @Test
    public void testExtractBuildsSummaryAndStateLevelProperties() {
        DefaultSemanticStateExtractor extractor = new DefaultSemanticStateExtractor();
        StateStub state = new StateStub();
        state.set(Tags.Title, "Login page");
        state.set(Tags.Desc, "Page description");
        state.set(Tags.LinkReference, "https://para.testar.org");

        SemanticStateRecord record = extractor.extract(state);

        Assert.assertEquals("Login page", record.getProperties().get("title"));
        Assert.assertEquals("https://para.testar.org", record.getProperties().get("link"));
        Assert.assertEquals("Page description", record.getProperties().get("description"));
    }
}
