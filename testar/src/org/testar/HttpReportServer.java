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
	private File outputDirectory;

	private ShutdownServlet shutdownServlet;

	public HttpReportServer(File outputDirectory) {
		this.outputDirectory = outputDirectory;
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

			// Run output that contains all resources
			resourceHandler.setResourceBase(outputDirectory.getCanonicalPath());
			System.out.println("HttpReportServer aiming to: " + outputDirectory);

			// the webapp context handler will handle requests for our controllers
			WebAppContext webAppContext = new WebAppContext();
			webAppContext.setContextPath("/");
			webAppContext.setResourceBase(outputDirectory.getCanonicalPath());
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
			e.printStackTrace();
		}
	}

	public boolean isJettyServerRunning() {
		return shutdownServlet.isServerRunning();
	}

} 
