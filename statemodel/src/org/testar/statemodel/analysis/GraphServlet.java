/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.analysis;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class GraphServlet extends HttpServlet {

    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        boolean abstractLayerRequired, concreteLayerRequired, sequenceLayerRequired, showCompoundGraph = false;
        abstractLayerRequired = request.getParameter("abstractoption") != null;
        concreteLayerRequired = request.getParameter("concreteoption") != null;
        sequenceLayerRequired = request.getParameter("sequenceoption") != null;
        showCompoundGraph = request.getParameter("compoundoption") != null;
        String modelIdentifier = request.getParameter("modelIdentifier");
        String concreteStateIdentifier = request.getParameter("concrete_state_id");

        // fetch the analysismanager from the servlet context
        ServletContext servletContext = getServletContext();
        // todo: this needs proper error handling
        AnalysisManager analysisManager = (AnalysisManager) servletContext.getAttribute("analysisManager");

        if (modelIdentifier != null) {
            // this is the controller logic for the overall model graph

            // check if there were any layers requested
            if (!(abstractLayerRequired || concreteLayerRequired || sequenceLayerRequired)) {
                response.sendRedirect("/models");
            }

            // fetch the model
            String jsonFileName = analysisManager.fetchGraphForModel(modelIdentifier, abstractLayerRequired, concreteLayerRequired, sequenceLayerRequired, showCompoundGraph);

            try {
                request.setAttribute("graphContentFile", jsonFileName);
                request.setAttribute("contentFolder", modelIdentifier);
                RequestDispatcher dispatcher = servletContext.getRequestDispatcher("/graph.jsp");
                dispatcher.forward(request, response);
            } catch (Throwable throwable) {
                StateModelDebugLog.log("GraphServlet.doPost failed while rendering a model graph.", throwable);
                throw new ServletException("Unable to render model graph", throwable);
            }
        } else if (concreteStateIdentifier != null) {
            // this is the controller logic for the widget tree graph.
            // if more parts are added, we should separate this logic into separate controllers

            // fetch the model
            String jsonFileName = analysisManager.fetchWidgetTree(concreteStateIdentifier);

            try {
                request.setAttribute("graphContentFile", jsonFileName);
                request.setAttribute("contentFolder", concreteStateIdentifier);
                RequestDispatcher dispatcher = servletContext.getRequestDispatcher("/graph.jsp");
                dispatcher.forward(request, response);
            } catch (Throwable throwable) {
                StateModelDebugLog.log("GraphServlet.doPost failed while rendering a widget graph.", throwable);
                throw new ServletException("Unable to render widget graph", throwable);
            }
        } else {
            response.sendRedirect("/models");
        }
    }

}
