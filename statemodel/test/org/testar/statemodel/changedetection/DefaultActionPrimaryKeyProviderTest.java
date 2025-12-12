package org.testar.statemodel.changedetection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DefaultActionPrimaryKeyProviderTest {

    @Test
    public void testDefaultActionIdAsPrimaryKey() {
        DefaultActionPrimaryKeyProvider provider = new DefaultActionPrimaryKeyProvider();
        assertEquals("AA1", provider.getPrimaryKey("AA1"));
    }

}
