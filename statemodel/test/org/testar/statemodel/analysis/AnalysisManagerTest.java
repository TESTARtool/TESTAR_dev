package org.testar.statemodel.analysis;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AnalysisManagerTest {

    @Test
    public void testExportPropertyNameProtectsGraphId() {
        assertEquals("attributeId", AnalysisManager.exportPropertyName("id"));
        assertEquals("widgetId", AnalysisManager.exportPropertyName("widgetId"));
    }
}
