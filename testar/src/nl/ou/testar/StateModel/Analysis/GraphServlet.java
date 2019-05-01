package nl.ou.testar.StateModel.Analysis;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GraphServlet  extends HttpServlet {

    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        boolean abstractLayerRequired, concreteLayerRequired, sequenceLayerRequired, showCompoundGraph = false;
        abstractLayerRequired = request.getParameter("abstractoption") != null;
        concreteLayerRequired = request.getParameter("concreteoption") != null;
        sequenceLayerRequired = request.getParameter("sequenceoption") != null;
        showCompoundGraph = request.getParameter("compoundgraph") != null;
        String modelIdentifier = request.getParameter("modelIdentifier");

        if (modelIdentifier == null || (!abstractLayerRequired && !concreteLayerRequired && !sequenceLayerRequired)) {
            response.sendRedirect("/models");
        }

        ServletContext servletContext = getServletContext();
        // todo: this needs proper error handling
        AnalysisManager analysisManager = (AnalysisManager)servletContext.getAttribute("analysisManager");

        String jsonFileName = analysisManager.fetchGraphForModel(modelIdentifier, abstractLayerRequired, concreteLayerRequired, sequenceLayerRequired, showCompoundGraph);

        try {
            request.setAttribute("graphContentFile", jsonFileName);
            request.setAttribute("modelIdentifier", modelIdentifier);
            RequestDispatcher dispatcher = servletContext.getRequestDispatcher("/graph.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
