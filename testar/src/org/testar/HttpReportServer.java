/***************************************************************************************************
 *
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
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

package org.testar;

import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import nl.ou.testar.StateModel.Analysis.ShutdownServlet;

public class HttpReportServer { 

	private int portNumber = 8091;
	private File htmlReport;

	private ShutdownServlet shutdownServlet;

	public HttpReportServer(File htmlReport) {
		this.htmlReport = htmlReport;
	}	

	/**
	 * Run a Jetty Server that allows remote Verification of HTML reports. 
	 * localhost:8091
	 */
	public void runHtmlReport() {
		System.out.println("HTML report server ready!");
		System.out.println("Go to localhost:" + portNumber);

		try {
			Server server = new Server();
			ServerConnector connector = new ServerConnector(server);
			connector.setPort(portNumber);
			server.setConnectors(new Connector[]{connector});

			ResourceHandler resourceHandler = new ResourceHandler();
			resourceHandler.setDirectoriesListed(true);

			// Specific HTML report directory
			// bin\\output\\2020-07-24_09h22m12s_notepad_1\\HTMLreports\\2020-07-24_09h22m12s_notepad_1_sequence_1.html
			String htmlPath = htmlReport.getCanonicalPath();

			// Run output that contains all resources
			String runPath = getDynamicOutputDirectory(htmlPath);
			resourceHandler.setResourceBase(runPath);
			System.out.println("HttpReportServer aiming to: " + runPath);

			// the webapp context handler will handle requests for our controllers
			WebAppContext webAppContext = new WebAppContext();
			webAppContext.setContextPath("/");
			webAppContext.setResourceBase(runPath);
			// Shutdown this server
			shutdownServlet = new ShutdownServlet(server);
			webAppContext.addServlet(new ServletHolder(shutdownServlet), "/shutdown");

			HandlerList handlers = new HandlerList();
			handlers.addHandler(resourceHandler);
			handlers.addHandler(webAppContext);
			handlers.addHandler(new DefaultHandler());
			server.setHandler(handlers);

			server.setStopTimeout(10000L);
			server.start();

		} catch (Exception e) {
			System.out.println("Error running HTML report");
		}
	}

	public boolean isJettyServerRunning() {
		return shutdownServlet.isServerRunning();
	}

	/**
	 * Get dynamic output content : 2020-07-24_09h22m12s_notepad_1
	 * Using default structure : \\output\\2020-07-24_09h22m12s_notepad_1\\HTMLreports\\
	 * 
	 * @return
	 */
	private String getDynamicOutputDirectory(String htmlPath) {
		String runDirectory = "";
		try {
			String startIndex = File.separator + "output" + File.separator;
			String endIndex = File.separator + "HTMLreports";
			runDirectory = htmlPath.substring(htmlPath.lastIndexOf(startIndex) + 1, htmlPath.lastIndexOf(endIndex));

			return new File(runDirectory).getCanonicalPath();
		} catch (IOException e) {
			System.out.println("ERROR: getDynamicOutputDirectory: from : " + htmlPath);
		}

		return runDirectory;
	}

} 
