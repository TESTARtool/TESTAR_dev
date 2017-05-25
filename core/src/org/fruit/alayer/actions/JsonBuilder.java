package org.fruit.alayer.actions;

import org.fruit.alayer.Role;

/**
 * Created by floren on 25-5-2017.
 */
public class JsonBuilder {

    private static String build(String name) {
        return name + ": ";
    }

    public static String build(String name, Object object) {
        if (object instanceof String) {
            return build(name,(String)object);
        } else if (object instanceof Role) {
            return build(name,(Role)object);
        } else {
            throw new UnsupportedOperationException("Type not supported: " + object.getClass().getName());
        }
    }

    private static String build(String name, Role role) {
        return build(name) + role.name() ;
    }

    private static String build(String name, String text) {
        return build(name) + text;
    }

}
