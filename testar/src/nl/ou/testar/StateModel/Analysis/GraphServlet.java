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
        showCompoundGraph = request.getParameter("compoundoption") != null;
        String modelIdentifier = request.getParameter("modelIdentifier");
        String concreteStateIdentifier = request.getParameter("concrete_state_id");

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (concreteStateIdentifier != null) {
            // this is the controller logic for the widget tree graph.
            // if more parts are added, we should separate this logic into separate controllers

            // fetch the model
            String jsonFileName = analysisManager.fetchWidgetTree(concreteStateIdentifier);

            try {
                request.setAttribute("graphContentFile", jsonFileName);
                request.setAttribute("contentFolder", concreteStateIdentifier);
                RequestDispatcher dispatcher = servletContext.getRequestDispatcher("/graph.jsp");
                dispatcher.forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            response.sendRedirect("/models");
        }






    }

}
