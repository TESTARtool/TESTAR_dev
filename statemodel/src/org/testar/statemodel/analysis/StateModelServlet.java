/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.analysis;

import org.testar.statemodel.analysis.representation.AbstractStateModel;
import org.testar.statemodel.analysis.representation.ActionViz;

import com.orientechnologies.orient.core.exception.OCommandExecutionException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class StateModelServlet extends HttpServlet {

    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        ServletContext servletContext = getServletContext();
        // todo: this needs proper error handling
        // fetch a list of the generated models from the analysismanager
        AnalysisManager analysisManager = (AnalysisManager) servletContext.getAttribute("analysisManager");

        try {
            List<AbstractStateModel> models = analysisManager.fetchModels();
            request.setAttribute("models", models);
            RequestDispatcher dispatcher = servletContext.getRequestDispatcher("/models.jsp");
            dispatcher.forward(request, response);
        } catch (OCommandExecutionException e) {
            // If user tried to analyze an OrientDB database without models, launch an info web page
            if (e.getMessage() != null && e.getMessage().contains("Class not found: AbstractStateModel")) {
                System.out.println("Database does not contain any model to visualize");
                RequestDispatcher dispatcher = servletContext.getRequestDispatcher("/warning.jsp");
                dispatcher.forward(request, response);
            } else {
                StateModelDebugLog.log("State model query failed while loading /models.", e);
                throw e;
            }
        } catch (Throwable throwable) {
            StateModelDebugLog.log("StateModelServlet.doGet failed.", throwable);
            throw new ServletException("Unable to render state model page", throwable);
        }
    }

    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        AnalysisManager analysisManager = (AnalysisManager) servletContext.getAttribute("analysisManager");

        try {
            String sequenceId = request.getParameter("sequenceId");
            List<ActionViz> visualizations = analysisManager.fetchTestSequence(sequenceId);
            request.setAttribute("contentFolder", sequenceId);
            request.setAttribute("visualizations", visualizations);

            RequestDispatcher dispatcher = servletContext.getRequestDispatcher("/sequence.jsp");
            dispatcher.forward(request, response);
        } catch (Throwable throwable) {
            StateModelDebugLog.log("StateModelServlet.doPost failed.", throwable);
            throw new ServletException("Unable to render test sequence preview", throwable);
        }
    }
}
