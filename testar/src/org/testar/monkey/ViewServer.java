/***************************************************************************************************
 *
 * Copyright (c) 2024 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2024 Open Universiteit - www.ou.nl
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

package org.testar.monkey;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

public class ViewServer extends HttpServlet {

	private static final long serialVersionUID = 5404337152725715903L;

	private String outputDir = "";
	private String outputViewDir = "";

	public ViewServer(String outputDir) {
		this.outputDir = outputDir;
		// check if the output directory has a trailing line separator
		if (!this.outputDir.substring(this.outputDir.length() - 1).equals(File.separator)) {
			this.outputDir += File.separator;
		}
		this.outputViewDir = this.outputDir + "view" + File.separator;
	}

	public void runViewServer() {
		try {
			startViewServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startViewServer() throws Exception {
		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(8099);
		server.addConnector(connector);

		// the resource handler will handle static file requests
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setResourceBase(outputViewDir);

		// the webapp context handler will handle requests for our controllers
		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setResourceBase(outputViewDir);
		webAppContext.addServlet(new ServletHolder(new ViewServer(outputDir)), "/view");
		webAppContext.addServlet(new ServletHolder(new ViewServerSummary(outputViewDir)), "/viewSummary");

		Configuration.ClassList classlist = Configuration.ClassList
				.setServerDefault( server );
		classlist.addBefore(
				"org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
				"org.eclipse.jetty.annotations.AnnotationConfiguration" );
		webAppContext.setAttribute(
				"org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
				".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$" );

		// create a handler list and pass it to the server to start it
		HandlerList handlerList = new HandlerList();
		handlerList.addHandler(resourceHandler);
		handlerList.addHandler(webAppContext);
		handlerList.addHandler(new DefaultHandler());

		server.setHandler(handlerList);
		server.start();
		openBrowser("http://localhost:8099/view");
		server.join();
	}

	private void openBrowser(String url) {
		try {
			java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		sendOutputFoldersList(request, response);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/view.jsp");
		dispatcher.forward(request, response);
	}

	private void sendOutputFoldersList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		File outputFile = new File(outputDir);
		// Check if the directory exists and is a directory
		if (outputFile.exists() && outputFile.isDirectory()) {
			String[] folders = outputFile.list();
			//TODO: Re-implement this ignoring functionality
			List<String> ignoreList = Arrays.asList("graphs", "metrics", "temp", "view");

			// Convert the array to a list for better manipulation
			List<String> folderList = new ArrayList<>();
			if (folders != null) {
				for (String folder : folders) {
					// Add only directories to the list
					if (new File(outputFile, folder).isDirectory() && !ignoreList.contains(folder)) {
						folderList.add(folder);
					}
				}
			}

			// Set the folder list as a request attribute
			request.setAttribute("folderList", folderList);

			// Forward the request to the view.jsp page
			request.getRequestDispatcher("/view.jsp").forward(request, response);
		} else {
			// Handle the case where the directory doesn't exist or is not a directory
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write("Directory not found");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] selectedFolders = request.getParameterValues("selectedFolders");

		if (selectedFolders != null && selectedFolders.length > 0) {
			List<String> htmlReportsList = getAllOutputHTMLreports(selectedFolders);

			// Invoke ViewServerSummary servlet
			request.setAttribute("htmlReportsList", htmlReportsList);
			request.getRequestDispatcher("/viewSummary").forward(request, response);
		} else {
			System.out.println("No folders selected");
		}
	}

	private List<String> getAllOutputHTMLreports(String[] selectedFolders) throws IOException{
		List<String> htmlReportsList = new ArrayList<>();

		for (String folder : selectedFolders) {
			File sequenceFolder = new File(outputDir + folder + File.separator + "reports");
			if(sequenceFolder.exists() && sequenceFolder.isDirectory()) {
				File[] files = sequenceFolder.listFiles();
				if (files != null) {
					for (File file : files) {
						// Check if the file is an HTML file
						if (file.isFile() && file.getName().toLowerCase().endsWith(".html")) {
							// Add the HTML file to the list
							htmlReportsList.add(file.getCanonicalPath().replace("\\", "/"));
						}
					}
				}
			}
		}

		return htmlReportsList;
	}

}
