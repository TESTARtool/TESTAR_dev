package org.testar.statemodel.changedetection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DefaultActionDescriptionProviderTest {

    @Test
    public void testDefaultActionIdAsDescription() {
        DefaultActionDescriptionProvider provider = new DefaultActionDescriptionProvider();
        assertEquals("AA1", provider.getDescription("AA1"));
    }

}
