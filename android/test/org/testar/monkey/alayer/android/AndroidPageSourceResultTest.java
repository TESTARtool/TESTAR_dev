package org.testar.monkey.alayer.android;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;

public class AndroidPageSourceResultTest {

    @Test
    public void resultWithDocument_reportsDocumentAndFeedback() {
        Document document = Mockito.mock(Document.class);
        AndroidPageSourceResult result = new AndroidPageSourceResult(document, "");

        Assert.assertTrue(result.hasDocument());
        Assert.assertSame(document, result.getDocument());
        Assert.assertEquals("", result.getFeedback());
    }

    @Test
    public void resultWithoutDocument_reportsMissingDocumentAndFeedback() {
        AndroidPageSourceResult result = new AndroidPageSourceResult(null, "page source failed");

        Assert.assertFalse(result.hasDocument());
        Assert.assertNull(result.getDocument());
        Assert.assertEquals("page source failed", result.getFeedback());
    }
}
