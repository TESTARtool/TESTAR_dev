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

package org.testar.statemodel.util;

import org.testar.statemodel.persistence.orientdb.entity.Property;
import org.testar.statemodel.persistence.orientdb.entity.TypeConvertor;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.TaggableBase;

import java.util.Set;
import java.util.zip.CRC32;

public abstract class HydrationHelper {

    /**
     * Returns a tag for a given tag name
     * @param tags
     * @param tagName
     * @return
     */
    public static Tag<?> getTag(TaggableBase tags, String tagName) {
        Tag<?> tag = null;
        for (Tag t : tags.tags()) {
            if (t.name().equals(tagName)) {
                tag = t;
                break;
            }
        }
        return tag;
    }

    public static boolean typesMatch(Property property, Class clazz) {
        return property.getPropertyType() == TypeConvertor.getInstance().getOrientDBType(clazz);
    }

    /**
     * This method searches a set of properties for a given property name and returns it if found.
     * @param properties
     * @param propertyName
     * @return
     */
    public static Property getProperty(Set<Property> properties, String propertyName) {
        Property property = null;
        for (Property prop : properties) {
            if (prop.getPropertyName().equals(propertyName)) {
                property = prop;
                break;
            }
        }
        return property;
    }

    /**
     * This method returns a unique identifier for a given input string.
     * @param text
     * @return
     */
    public static String lowCollisionID(String text){ // reduce ID collision probability
        CRC32 crc32 = new CRC32(); crc32.update(text.getBytes());
        return Integer.toUnsignedString(text.hashCode(), Character.MAX_RADIX) +
                Integer.toHexString(text.length()) +
                crc32.getValue();
    }

    /**
     * This method returns a unique id for an action/transition that can be used in OrientDB.
     * @param sourceId
     * @param targetId
     * @param actionId
     * @param modelIdentifier
     * @return
     */
    public static String createOrientDbActionId(String sourceId, String targetId, String actionId, String modelIdentifier) {
        // this creates a unique id that is needed for OrientDB storage
        String id = sourceId + "-" + actionId + "-" + targetId;
        if (modelIdentifier != null) {
            id += "-" + modelIdentifier;
        }
        return lowCollisionID(sourceId + "-" + actionId + "-" + targetId + "-" + modelIdentifier);
    }

}
