package org.testar.statemodel.persistence.orientdb.entity;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TypeConvertorTest {

    private TypeConvertor convertor;

    @Before
    public void setUp() {
        convertor = TypeConvertor.getInstance();
    }

    @Test
    public void testSingletonInstanceIsNotNull() {
        assertNotNull(convertor);
        assertSame(convertor, TypeConvertor.getInstance());
    }

    @Test
    public void testJavaToOrientDbMappings() {
        assertEquals(OType.STRING, convertor.getOrientDBType(String.class));
        assertEquals(OType.BOOLEAN, convertor.getOrientDBType(Boolean.class));
        assertEquals(OType.DOUBLE, convertor.getOrientDBType(Double.class));
        assertEquals(OType.FLOAT, convertor.getOrientDBType(Float.class));
    }

    @Test
    public void testOrientDbToJavaMappings() {
        assertEquals(String.class, convertor.getClass(OType.STRING));
        assertEquals(Boolean.class, convertor.getClass(OType.BOOLEAN));
        assertEquals(Double.class, convertor.getClass(OType.DOUBLE));
        assertEquals(Float.class, convertor.getClass(OType.FLOAT));
    }

    @Test
    public void testUnknownJavaTypeReturnsNull() {
        assertNull(convertor.getOrientDBType(Integer.class));
    }

    @Test
    public void testUnknownOTypeReturnsNull() {
        assertNull(convertor.getClass(OType.INTEGER));
    }

}
