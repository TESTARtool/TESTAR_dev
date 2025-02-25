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

import org.testar.statemodel.analysis.representation.AbstractStateModel;
import org.testar.statemodel.analysis.representation.ActionViz;

import com.orientechnologies.orient.core.exception.OCommandExecutionException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        AnalysisManager analysisManager = (AnalysisManager)servletContext.getAttribute("analysisManager");

        try {
        	List<AbstractStateModel> models = analysisManager.fetchModels();
        	request.setAttribute("models", models);
        	RequestDispatcher dispatcher = servletContext.getRequestDispatcher("/models.jsp");
        	dispatcher.forward(request, response);
        } catch (ServletException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        } catch (OCommandExecutionException e) {
        	// If user tried to analyze an OrientDB database without models, launch an info web page
        	if(e.getMessage() != null && e.getMessage().contains("Class not found: AbstractStateModel")) {
        		System.out.println("Database does not contain any model to visualize");
        		RequestDispatcher dispatcher = servletContext.getRequestDispatcher("/warning.jsp");
        		dispatcher.forward(request, response);
        	} else {
        		e.printStackTrace();
        	}
        }

    }

    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        AnalysisManager analysisManager = (AnalysisManager)servletContext.getAttribute("analysisManager");

        try {
            String sequenceId = request.getParameter("sequenceId");
            List<ActionViz> visualizations = analysisManager.fetchTestSequence(sequenceId);
            request.setAttribute("contentFolder", sequenceId);
            request.setAttribute("visualizations", visualizations);

            RequestDispatcher dispatcher = servletContext.getRequestDispatcher("/sequence.jsp");
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
