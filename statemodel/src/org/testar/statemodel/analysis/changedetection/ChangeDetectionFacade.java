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

package org.testar.statemodel.analysis.changedetection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testar.statemodel.analysis.AnalysisManager;
import org.testar.statemodel.analysis.jsonformat.Element;
import org.testar.statemodel.changedetection.ChangeDetectionResult;
import org.testar.statemodel.changedetection.OrientDbActionPrimaryKeyProvider;
import org.testar.statemodel.persistence.PersistenceManager;
import org.testar.statemodel.persistence.orientdb.OrientDBManager;
import org.testar.statemodel.persistence.orientdb.entity.EntityManager;
import org.testar.statemodel.util.EventHelper;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Facade for change detection analysis:
 * - build a persistence manager to extract abstract state models
 * - run the change detection engine
 * - load graph elements for old/new models
 * - merge them into a change-detection visualization graph
 */
final class ChangeDetectionFacade {

    private final AnalysisManager analysisManager;
    private final ObjectMapper mapper;

    public ChangeDetectionFacade(AnalysisManager analysisManager, ObjectMapper mapper) {
        this.analysisManager = Objects.requireNonNull(analysisManager, "analysisManager");
        this.mapper = Objects.requireNonNull(mapper, "mapper");
    }

    public ChangeDetectionResult compare(String oldModelId, String newModelId) {
        PersistenceManager pm = buildPersistenceManager();
        try {
            ChangeDetectionAnalysisService service = new ChangeDetectionAnalysisService(pm);
            return service.compare(oldModelId, newModelId);
        } finally {
            pm.shutdown();
        }
    }

    public List<Map<String, Object>> buildMergedGraph(String oldModelId, String newModelId) {
        // Run comparison first and close the PM before loading graphs (prevents local plocal locks).
        PersistenceManager pm = buildPersistenceManager();
        ChangeDetectionResult result;
        try {
            ChangeDetectionAnalysisService service = new ChangeDetectionAnalysisService(pm);
            result = service.compare(oldModelId, newModelId);
        } finally {
            pm.shutdown();
        }

        List<Map<String, Object>> oldElements = loadGraphElements(oldModelId);
        List<Map<String, Object>> newElements = loadGraphElements(newModelId);

        EntityManager entityManager = new EntityManager(analysisManager.getDbConfig());
        try {
            OrientDbActionPrimaryKeyProvider actionPrimaryKeyProvider = new OrientDbActionPrimaryKeyProvider(entityManager.getConnection());
            ChangeDetectionGraphBuilder graphBuilder = new ChangeDetectionGraphBuilder(actionPrimaryKeyProvider);
            return graphBuilder.build(oldModelId, newModelId, oldElements, newElements, result);
        } finally {
            entityManager.releaseConnection();
        }
    }

    private PersistenceManager buildPersistenceManager() {
        EntityManager entityManager = new EntityManager(analysisManager.getDbConfig());
        return new OrientDBManager(new EventHelper(), entityManager);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> loadGraphElements(String modelId) {
        List<Element> elements = analysisManager.fetchGraphElementsForModel(modelId, true, true, false, false);
        return mapper.convertValue(elements, List.class);
    }

}
