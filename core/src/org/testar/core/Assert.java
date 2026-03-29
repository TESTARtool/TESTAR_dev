/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core;

import java.util.Collection;

public final class Assert {

    private Assert() { }
    
    public static void isTrue(boolean expression, String text) {
        if (!expression) {
            throw new IllegalArgumentException(text);
        }
    }
    
    public static void isTrue(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException("You passed illegal parameters!");
        }
    }

    public static void isEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new IllegalArgumentException(expected + " != " + actual);
        }
    }

    public static void isEquals(Object expected, Object actual, String text) {
        if (!expected.equals(actual)) {
            throw new IllegalArgumentException(text);
        }
    }
        
    public static void notNull(Object object, String text) {
        if (object == null) {
            throw new IllegalArgumentException(text);
        }
    }

    public static <T> T notNull(T object) {
        if (object == null) {
            throw new IllegalArgumentException("You passed null as a parameter!");
        }
        return object;
    }
    
    public static void notNull(Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            throw new IllegalArgumentException("You passed null as a parameter!");
        }
    }

    public static void notNull(Object o1, Object o2, Object o3) {
        if (o1 == null || o2 == null || o3 == null) {
            throw new IllegalArgumentException("You passed null as a parameter!");
        }
    }

    public static void hasText(String string) {
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException("You passed a null or empty string!");
        }
    }

    public static void hasTextSetting(String string, String settingName) {
        if (string == null || string.length() == 0) {
            String message = "Non valid setting value!\n"
                    + String.format("It seems that setting %s as null or empty string!\n", settingName)
                    + "Please provide a correct string value using TESTAR GUI or test.setting file";
            throw new IllegalArgumentException(message);
        }
    }

    public static void collectionContains(Collection<String> collection, String value) {
        if (!collection.contains(value)) {
            String message = String.format("Collection %s doesn't contain desired value %s", collection.toString(), value);
            throw new IllegalArgumentException(message);
        }
    }

    public static void collectionNotContains(Collection<String> collection, String value) {
        if (collection.contains(value)) {
            String message = String.format("Collection %s contains undesired value %s", collection.toString(), value);
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void collectionSize(Collection<String> collection, int size) {
        if (collection.size() != size) {
            String message = String.format("Collection %s has undesired size %s", collection.toString(), size);
            throw new IllegalArgumentException(message);
        }
    }
}
