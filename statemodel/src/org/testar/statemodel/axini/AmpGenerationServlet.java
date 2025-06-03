package org.testar.statemodel.axini;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;

public class AmpGenerationServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");

		try {
			// Read JSON from request body
			StringBuilder jsonBuilder = new StringBuilder();
			BufferedReader reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonBuilder.append(line);
			}

			String json = jsonBuilder.toString();

			// Transform JSON to AMP
			GuiStateModel guiModel = GuiModelLoader.loadFromJson(json);
			ProcessDefinition process = AmpBuilder.buildFrom(guiModel);
			String ampCode = AmpCodeGenerator.generate(process);

			// Write AMP code to response
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(ampCode);

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("Error generating AMP: " + e.getMessage());
		}
	}
}
