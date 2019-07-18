package nl.ou.testar.StateModel.Analysis;

import nl.ou.testar.StateModel.Analysis.Representation.AbstractStateModel;

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
}
