package org.testar.statemodel.persistence.orientdb.entity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigTest {

    private Config config;

    @Before
    public void setUp() {
        config = new Config();
    }

    @Test
    public void testConnectionType() {
        config.setConnectionType(Config.CONNECTION_TYPE_LOCAL);
        assertEquals(Config.CONNECTION_TYPE_LOCAL, config.getConnectionType());

        config.setConnectionType(Config.CONNECTION_TYPE_REMOTE);
        assertEquals(Config.CONNECTION_TYPE_REMOTE, config.getConnectionType());
    }

    @Test
    public void testServer() {
        config.setServer("localhost");
        assertEquals("localhost", config.getServer());
    }

    @Test
    public void testDatabase() {
        config.setDatabase("testDB");
        assertEquals("testDB", config.getDatabase());
    }

    @Test
    public void testUser() {
        config.setUser("user");
        assertEquals("user", config.getUser());
    }

    @Test
    public void testPassword() {
        config.setPassword("pass");
        assertEquals("pass", config.getPassword());
    }

    @Test
    public void testResetDataStoreFlag() {
        config.setResetDataStore(true);
        assertTrue(config.resetDataStore());

        config.setResetDataStore(false);
        assertFalse(config.resetDataStore());
    }

    @Test
    public void testDatabaseDirectory() {
        config.setDatabaseDirectory("/data/db");
        assertEquals("/data/db", config.getDatabaseDirectory());
    }

}
