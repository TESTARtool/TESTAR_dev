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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Builds and returns the merged change-detection graph for the frontend.
 */
public class ChangeDetectionGraphServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String oldModelId = req.getParameter("oldModelIdentifier");
        String newModelId = req.getParameter("newModelIdentifier");

        if (oldModelId == null || newModelId == null || oldModelId.trim().isEmpty() || newModelId.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Both oldModelIdentifier and newModelIdentifier are required.");
            return;
        }

        ServletContext ctx = getServletContext();
        AnalysisManager analysisManager = (AnalysisManager) ctx.getAttribute("analysisManager");

        PersistenceManager pm = buildPersistenceManager(analysisManager);
        boolean pmClosed = false;
        try {
            ChangeDetectionAnalysisService service = new ChangeDetectionAnalysisService(pm);
            ChangeDetectionResult result = service.compare(oldModelId, newModelId);

            // close persistence used by the comparator to avoid plocal OrientDB lock conflicts
            pm.shutdown();
            pmClosed = true;

            List<Map<String, Object>> oldElements = loadGraphElements(analysisManager, oldModelId);
            List<Map<String, Object>> newElements = loadGraphElements(analysisManager, newModelId);

            // prepare the action primary key provider and builder to merge
            EntityManager entityManager = new EntityManager(analysisManager.getDbConfig());
            try {
                OrientDbActionPrimaryKeyProvider actionPrimaryKeyProvider = new OrientDbActionPrimaryKeyProvider(entityManager.getConnection());
                ChangeDetectionGraphBuilder graphBuilder = new ChangeDetectionGraphBuilder(actionPrimaryKeyProvider);
                List<Map<String, Object>> merged = graphBuilder.build(oldModelId, newModelId, oldElements, newElements, result);
                resp.setContentType("application/json");
                resp.getWriter().write(mapper.writeValueAsString(merged));
            } finally {
                entityManager.releaseConnection();
            }
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Failed to build merged graph: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (!pmClosed) {
                pm.shutdown();
            }
        }
    }

    private PersistenceManager buildPersistenceManager(AnalysisManager analysisManager) {
        EntityManager entityManager = new EntityManager(analysisManager.getDbConfig());
        return new OrientDBManager(new EventHelper(), entityManager);
    }

    private List<Map<String, Object>> loadGraphElements(AnalysisManager analysisManager, String modelId) {
        List<Element> elements = analysisManager.fetchGraphElementsForModel(modelId, true, true, false, false);
        return mapper.convertValue(elements, List.class);
    }

}
