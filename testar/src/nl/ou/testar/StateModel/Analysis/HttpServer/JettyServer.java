/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2021 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2021 Universitat Politecnica de Valencia - www.upv.es
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

package nl.ou.testar.StateModel.Analysis.HttpServer;

import nl.ou.testar.StateModel.Analysis.AnalysisManager;
import nl.ou.testar.StateModel.Analysis.GraphServlet;
import nl.ou.testar.StateModel.Analysis.ShutdownServlet;
import nl.ou.testar.StateModel.Analysis.StateModelServlet;

import java.util.Date;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer {
    private Server server;
    private ShutdownServlet shutdownServlet;
    private long startTime;
    private long maxTime;

    public void setMaxTime(long maxTime) {
        startTime = new Date().getTime();
        this.maxTime = maxTime;
    }

    /**
     * Call this method to start running the jetty server.
     * @param resourceBase
     * @param analysisManager
     * @throws Exception
     */
    public void start(String resourceBase, AnalysisManager analysisManager) throws Exception {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8090);
        server.setConnectors(new Connector[]{connector});

        // the resource handler will handle static file requests
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase(resourceBase);

        // the webapp context handler will handle requests for our controllers
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setResourceBase(resourceBase);
        webAppContext.addServlet(new ServletHolder(new StateModelServlet()), "/models/models");
        webAppContext.addServlet(new ServletHolder(new GraphServlet()), "/models/graph");

        // Shutdown this server
        shutdownServlet = new ShutdownServlet(server);
        webAppContext.addServlet(new ServletHolder(shutdownServlet), "/models/shutdown");

        webAppContext.setAttribute("analysisManager", analysisManager);

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

        server.setStopTimeout(10000L);
        server.start();
    }

    public boolean isJettyServerRunning() {
        return shutdownServlet.isServerRunning();
    }

    public boolean reachedMaxRunningTime() {
        if((new Date().getTime() - startTime) > maxTime) {
            try {
                server.stop();
            } catch (Exception e) {
                System.out.println("Failed to stop Jetty server by MaxTime");
            }
            return true;
        }

        return false;
    }
}