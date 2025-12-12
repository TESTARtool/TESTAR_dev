/***************************************************************************************************
 *
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.statemodel.changedetection;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.testar.statemodel.persistence.orientdb.entity.Connection;

import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

/**
 * Connects with OrientDB to retrieve an action description (ConcreteAction) for the given actionId.
 * Falls back to returning the abstract action id if no description is found.
 * 
 * This connection class is needed because the model extractor only provides the Abstract layer.
 */
public class OrientDbActionDescriptionProvider implements ActionDescriptionProvider {

    private final Connection connection;
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public OrientDbActionDescriptionProvider(Connection connection) {
        this.connection = Objects.requireNonNull(connection, "connection cannot be null");
    }

    @Override
    public String getDescription(String abstractActionId) {
        String cached = cache.get(abstractActionId);
        if (cached != null) {
            return cached;
        }
        String description = queryConcreteActionDescription(abstractActionId);
        String resolved = (description == null || description.isEmpty()) ? abstractActionId : description;
        cache.put(abstractActionId, resolved);
        return resolved;
    }

    /**
     * Queries OrientDB for a ConcreteAction description matching the given abstract action id.
     */
    protected String queryConcreteActionDescription(String abstractActionId) {
        try {
            String desc = fetchFirstConcreteDescription(abstractActionId);
            return desc;
        } catch (Exception e) {
            return null;
        }
    }

    private String fetchFirstConcreteDescription(String abstractActionId) {
        for (String concreteId : fetchConcreteActionIds(abstractActionId)) {
            String desc = fetchConcreteDescription(concreteId);
            if (desc != null && !desc.isEmpty()) {
                return desc;
            }
        }
        return null;
    }

    private java.util.List<String> fetchConcreteActionIds(String abstractActionId) {
        java.util.List<String> idsList = new java.util.ArrayList<>();
        String sql = "SELECT concreteActionIds FROM AbstractAction WHERE actionId = ? LIMIT 1";
        try (OResultSet rs = connection.getDatabaseSession().query(sql, abstractActionId)) {
            if (rs.hasNext()) {
                OResult result = rs.next();
                Object ids = result.getProperty("concreteActionIds");
                collectIds(idsList, ids);
            }
        }
        return idsList;
    }

    private String fetchConcreteDescription(String concreteActionId) {
        String sql = "SELECT `Desc` FROM ConcreteAction WHERE actionId = ? LIMIT 1";
        try (OResultSet rs = connection.getDatabaseSession().query(sql, concreteActionId)) {
            if (rs.hasNext()) {
                OResult result = rs.next();
                Object desc = result.getProperty("Desc");
                return desc != null ? desc.toString() : null;
            }
        }
        return null;
    }

    private void collectIds(java.util.List<String> target, Object ids) {
        if (ids == null) {
            return;
        }
        if (ids instanceof Iterable) {
            for (Object id : (Iterable<?>) ids) {
                if (id != null) {
                    target.add(id.toString());
                }
            }
        } else {
            String raw = ids.toString();
            if (raw.startsWith("[") && raw.endsWith("]") && raw.length() > 2) {
                target.add(raw.substring(1, raw.length() - 1));
            } else {
                target.add(raw);
            }
        }
    }

}
