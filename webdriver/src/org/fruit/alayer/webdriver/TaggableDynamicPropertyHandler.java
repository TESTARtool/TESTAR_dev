package org.fruit.alayer.webdriver;

import org.apache.commons.jxpath.DynamicPropertyHandler;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Taggable;

import java.util.stream.StreamSupport;

public class TaggableDynamicPropertyHandler implements DynamicPropertyHandler {
    protected Taggable t(Object o) {
        return (Taggable)o;
    }
    @Override
    public String[] getPropertyNames(Object object) {
        return StreamSupport.stream(t(object).tags().spliterator(), false).
                map(Tag::name).
                toArray(String[]::new);
    }

    @Override
    public Object getProperty(Object object, String propertyName) {
        return t(object).get(Tag.from(propertyName, Object.class), null);
    }

    @Override
    public void setProperty(Object object, String propertyName, Object value) {
        throw new RuntimeException("setting properties not allowed");
    }
}
