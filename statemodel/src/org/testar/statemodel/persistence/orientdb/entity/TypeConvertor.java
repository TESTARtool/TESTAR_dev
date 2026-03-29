/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.persistence.orientdb.entity;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class TypeConvertor {

    // map connecting Java types to OrientDb Types
    private final BiMap<Class<?>, OType> typeMatches;

    private static TypeConvertor instance;

    private TypeConvertor() {
        typeMatches = HashBiMap.create();

        // Manually preferred mappings
        typeMatches.put(String.class, OType.STRING);
        typeMatches.put(Boolean.class, OType.BOOLEAN);
        typeMatches.put(Double.class, OType.DOUBLE);
        typeMatches.put(Float.class, OType.FLOAT);
        typeMatches.put(Integer.class, OType.INTEGER);
        typeMatches.put(Long.class, OType.LONG);

        // Auto-add other defaults, if not already mapped
        for (OType oType : OType.values()) {
            Class<?> javaClass = oType.getDefaultJavaType();
            if (javaClass != null
                    && !typeMatches.containsKey(javaClass)
                    && !typeMatches.containsValue(oType)) {
                typeMatches.put(javaClass, oType);
            }
        }
    }

    public static TypeConvertor getInstance() {
        if (instance == null) {
            instance = new TypeConvertor();
        }
        return instance;
    }

    public OType getOrientDBType(Class<?> clazz) {
        return typeMatches.getOrDefault(clazz, OType.ANY);
    }

    public Class<?> getClass(OType oType) {
        return typeMatches.inverse().getOrDefault(oType, Object.class);
    }

}
