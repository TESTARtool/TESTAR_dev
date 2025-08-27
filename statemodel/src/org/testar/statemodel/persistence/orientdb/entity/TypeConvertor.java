/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

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
        typeMatches.put(Integer.class, OType.INTEGER);
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
