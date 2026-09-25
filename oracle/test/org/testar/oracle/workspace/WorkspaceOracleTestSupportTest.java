package org.testar.oracle.workspace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;
import org.testar.oracle.Oracle;

public class WorkspaceOracleTestSupportTest extends WorkspaceOracleTestSupport {

    @Test
    public void loadsShippedOracleThroughRuntimeLoaderAndCreatesFreshInstances() {
        Oracle first = loadWorkspaceOracle("WebAccessibilityFontSizeOracle");
        Oracle second = loadWorkspaceOracle("WebAccessibilityFontSizeOracle");

        assertEquals("WebAccessibilityFontSizeOracle", first.getClass().getSimpleName());
        assertNotSame(first, second);
    }
}
