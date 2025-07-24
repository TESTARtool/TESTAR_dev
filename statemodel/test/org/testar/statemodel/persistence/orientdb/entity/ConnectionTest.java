package org.testar.statemodel.persistence.orientdb.entity;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ConnectionTest {

    private OrientDB mockOrientDB;
    private Config config;
    private Connection connection;

    @Before
    public void setUp() {
        mockOrientDB = mock(OrientDB.class);
        config = new Config();
        config.setDatabase("testDB");
        config.setUser("user");
        config.setPassword("pass");
        connection = new Connection(mockOrientDB, config);
    }

    @Test
    public void testGetConfigReturnsExpectedConfig() {
        assertEquals(config, connection.getConfig());
    }

    @Test
    public void testGetDatabaseSessionCallsOrientDBOpen() {
        ODatabaseSession mockSession = mock(ODatabaseSession.class);
        when(mockOrientDB.open("testDB", "user", "pass")).thenReturn(mockSession);

        ODatabaseSession session = connection.getDatabaseSession();

        assertEquals(mockSession, session);
        verify(mockOrientDB).open("testDB", "user", "pass");
    }

    @Test
    public void testReleaseConnectionWhenOpen() {
        when(mockOrientDB.isOpen()).thenReturn(true);

        connection.releaseConnection();

        verify(mockOrientDB).close();
    }

    @Test
    public void testReleaseConnectionWhenNotOpen() {
        when(mockOrientDB.isOpen()).thenReturn(false);

        connection.releaseConnection();

        verify(mockOrientDB, never()).close();
    }

}
