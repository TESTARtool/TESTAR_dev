package org.testar.android;

import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class AndroidPageSourceResultTest {

    @Test
    public void preservesDocumentAndFeedback() {
        Document document = mock(Document.class);
        AndroidPageSourceResult success = new AndroidPageSourceResult(document, "");
        AndroidPageSourceResult failure = new AndroidPageSourceResult(null, "page source failed");

        assertSame(document, success.getDocument());
        assertEquals("", success.getFeedback());
        assertNull(failure.getDocument());
        assertEquals("page source failed", failure.getFeedback());
    }
}
