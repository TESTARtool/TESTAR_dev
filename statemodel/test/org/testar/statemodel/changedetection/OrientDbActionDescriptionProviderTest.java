package org.testar.statemodel.changedetection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.testar.statemodel.persistence.orientdb.entity.Connection;

public class OrientDbActionDescriptionProviderTest {

    @Test
    public void testObtainDescription() {
        OrientDbActionDescriptionProvider provider = new OrientDbActionDescriptionProvider(createDummyConnection()) {
            @Override
            protected String queryConcreteActionDescription(String abstractActionId) {
                return "desc-" + abstractActionId;
            }
        };
        assertEquals("desc-AA1", provider.getDescription("AA1"));
    }

    @Test
    public void testFallsBackToId() {
        OrientDbActionDescriptionProvider provider = new OrientDbActionDescriptionProvider(createDummyConnection()) {
            @Override
            protected String queryConcreteActionDescription(String abstractActionId) {
                return null;
            }
        };
        assertEquals("AA1", provider.getDescription("AA1"));
    }

    private Connection createDummyConnection() {
        return new Connection(null, null);
    }

}
