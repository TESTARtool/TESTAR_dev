/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.analysis.webserver;

import org.eclipse.jetty.ee10.annotations.AnnotationConfiguration;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.ee10.webapp.Configuration;
import org.eclipse.jetty.ee10.webapp.Configurations;
import org.eclipse.jetty.ee10.webapp.FragmentConfiguration;
import org.eclipse.jetty.ee10.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.ee10.webapp.JspConfiguration;
import org.eclipse.jetty.ee10.webapp.MetaInfConfiguration;
import org.eclipse.jetty.ee10.webapp.ServletsConfiguration;
import org.eclipse.jetty.ee10.webapp.WebAppConfiguration;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.ee10.webapp.WebInfConfiguration;
import org.eclipse.jetty.ee10.webapp.WebXmlConfiguration;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.testar.statemodel.analysis.AnalysisManager;
import org.testar.statemodel.analysis.GraphServlet;
import org.testar.statemodel.analysis.StateModelDebugLog;
import org.testar.statemodel.analysis.StateModelServlet;

public class JettyServer {
    private Server server;

    public boolean isRunning() {
        return server != null && server.isStarted();
    }

    /**
     * Call this method to start running the jetty server.
     * @param resourceBase
     * @param analysisManager
     * @throws Exception
     */
    public void start(String resourceBase, AnalysisManager analysisManager) throws Exception {
        server = new Server(); // the plain Exception is coming from 3rd party code
        Configurations.setServerDefault(server);
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8090);
        server.setConnectors(new Connector[]{connector});

        // The webapp context serves JSP pages, static assets, and registered servlets.
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setWar(resourceBase);
        webAppContext.setParentLoaderPriority(true);
        webAppContext.setAttribute(
            "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
            ".*/jakarta.servlet.jsp.jstl-api-[^/]*\\.jar$|.*/jakarta.servlet.jsp.jstl-[^/]*\\.jar$|.*/taglibs-standard-impl-[^/]*\\.jar$|.*/taglibs-standard-spec-[^/]*\\.jar$"
        );
        webAppContext.setConfigurations(new Configuration[]{
            new WebInfConfiguration(),
            new WebXmlConfiguration(),
            new MetaInfConfiguration(),
            new FragmentConfiguration(),
            new AnnotationConfiguration(),
            new ServletsConfiguration(),
            new JspConfiguration(),
            new JettyWebXmlConfiguration(),
            new WebAppConfiguration()
        });
        webAppContext.addServlet(new ServletHolder(new StateModelServlet()), "/models");
        webAppContext.addServlet(new ServletHolder(new GraphServlet()), "/graph");
        webAppContext.setAttribute("analysisManager", analysisManager);

        server.setHandler(webAppContext);
        server.start();
        StateModelDebugLog.log("State model analysis server started at http://localhost:8090/models using resource base: " + resourceBase);
    }
}