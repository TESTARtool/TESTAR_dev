package nl.ou.testar.StateModel.Analysis;

import nl.ou.testar.StateModel.Analysis.Representation.AbstractStateModel;
import nl.ou.testar.StateModel.Analysis.Representation.ActionViz;

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
        List<AbstractStateModel> models = analysisManager.fetchModels();

        try {
            request.setAttribute("models", models);
            RequestDispatcher dispatcher = servletContext.getRequestDispatcher("/models.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
