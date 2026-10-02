package org.testar.statemodel.analysis;

import org.junit.Test;
import org.mockito.MockedConstruction;
import com.orientechnologies.orient.core.db.OrientDB;
import org.testar.statemodel.persistence.orientdb.entity.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AnalysisManagerTest {

    @Test
    public void testExportPropertyNameProtectsGraphId() {
        assertEquals("attributeId", AnalysisManager.exportPropertyName("id"));
        assertEquals("widgetId", AnalysisManager.exportPropertyName("widgetId"));
    }

    @Test
    public void failedAnalysisStartupReleasesItsConnection() {
        Config config = new Config();
        config.setConnectionType("plocal");
        config.setDatabaseDirectory("database");
        config.setDatabase("testar");
        config.setUser("user");
        config.setPassword("password");
        try (MockedConstruction<OrientDB> connections = mockConstruction(OrientDB.class, (connection, context) -> {
            when(connection.isOpen()).thenReturn(true);
            when(connection.open("testar", "user", "password")).thenThrow(new IllegalStateException("cannot connect"));
        })) {
            try {
                new AnalysisManager(config, "output");
                fail("Analysis startup should report the connection failure");
            } catch (IllegalStateException exception) {
                assertEquals("cannot connect", exception.getMessage());
            }
            verify(connections.constructed().get(0)).close();
        }
    }
}
