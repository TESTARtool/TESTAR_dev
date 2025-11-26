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

package org.testar.statemodel.analysis;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

        String format = request.getParameter("format");

        // fetch the analysismanager from the servlet context
        ServletContext servletContext = getServletContext();
        // todo: this needs proper error handling
        AnalysisManager analysisManager = (AnalysisManager)servletContext.getAttribute("analysisManager");

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
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (concreteStateIdentifier != null && !"json".equalsIgnoreCase(format)) {
            // this is the controller logic for the widget tree graph.
            // if more parts are added, we should separate this logic into separate controllers

            // fetch the model
            String jsonFileName = analysisManager.fetchWidgetTree(concreteStateIdentifier);

            try {
                request.setAttribute("graphContentFile", jsonFileName);
                request.setAttribute("contentFolder", concreteStateIdentifier);
                RequestDispatcher dispatcher = servletContext.getRequestDispatcher("/graph.jsp");
                dispatcher.forward(request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (concreteStateIdentifier != null && "json".equalsIgnoreCase(format)) {
            String json = analysisManager.fetchWidgetTreeJson(concreteStateIdentifier);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json != null ? json : "[]");
        }
        else {
            response.sendRedirect("/models");
        }






    }

}
