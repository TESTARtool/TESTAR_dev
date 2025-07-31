package org.testar.statemodel.persistence.orientdb.entity;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Date;

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
        assertEquals(OType.INTEGER, convertor.getOrientDBType(Integer.class));
        assertEquals(OType.LONG, convertor.getOrientDBType(Long.class));
    }

    @Test
    public void testOrientDbToJavaMappings() {
        assertEquals(String.class, convertor.getClass(OType.STRING));
        assertEquals(Boolean.class, convertor.getClass(OType.BOOLEAN));
        assertEquals(Double.class, convertor.getClass(OType.DOUBLE));
        assertEquals(Float.class, convertor.getClass(OType.FLOAT));
        assertEquals(Integer.class, convertor.getClass(OType.INTEGER));
        assertEquals(Long.class, convertor.getClass(OType.LONG));
    }

    @Test
    public void testUnknownJavaTypeReturnsDefault() {
        assertNotNull(convertor.getOrientDBType(Date.class));
        assertEquals(OType.DATETIME, convertor.getOrientDBType(Date.class));
    }

    @Test
    public void testUnknownOTypeReturnsDefault() {
        assertNotNull(convertor.getClass(OType.DATETIME));
        assertEquals(Date.class, convertor.getClass(OType.DATETIME));
    }

    @Test
    public void testUnknownJavaTypeReturnsAny() {
        class UnknownType {}
        OType result = convertor.getOrientDBType(UnknownType.class);
        assertEquals(OType.ANY, result);
    }

    @Test
    public void testUnknownOTypeReturnsObjectClass() {
        Class<?> result = convertor.getClass(OType.LINKLIST);
        assertEquals(Object.class, result);
    }

}
