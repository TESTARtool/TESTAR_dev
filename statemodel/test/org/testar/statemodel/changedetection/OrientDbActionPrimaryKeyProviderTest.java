package org.testar.statemodel.changedetection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.testar.statemodel.persistence.orientdb.entity.Connection;

public class OrientDbActionPrimaryKeyProviderTest {

    @Test
    public void testObtainPrimaryKeyFromDescription() {
        OrientDbActionPrimaryKeyProvider provider = new OrientDbActionPrimaryKeyProvider(createDummyConnection()) {
            @Override
            protected String queryConcreteActionDescription(String abstractActionId) {
                return "desc-" + abstractActionId;
            }
        };
        assertEquals("desc-AA1", provider.getPrimaryKey("AA1"));
    }

    @Test
    public void testFallsBackToId() {
        OrientDbActionPrimaryKeyProvider provider = new OrientDbActionPrimaryKeyProvider(createDummyConnection()) {
            @Override
            protected String queryConcreteActionDescription(String abstractActionId) {
                return null;
            }
        };
        assertEquals("AA1", provider.getPrimaryKey("AA1"));
    }

    private Connection createDummyConnection() {
        return new Connection(null, null);
    }

}
