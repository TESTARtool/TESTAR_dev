package org.testar.statemodel.analysis.webserver;

import org.testar.statemodel.analysis.AnalysisManager;
import org.testar.statemodel.analysis.GraphServlet;
import org.testar.statemodel.analysis.StateModelServlet;
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

    /**
     * Call this method to start running the jetty server.
     * @param resourceBase
     * @param analysisManager
     * @throws Exception
     */
    public void start(String resourceBase, AnalysisManager analysisManager) throws Exception {
        server = new Server(); // the plain Exception is coming from 3rd party code
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
        webAppContext.addServlet(new ServletHolder(new StateModelServlet()), "/models");
        webAppContext.addServlet(new ServletHolder(new GraphServlet()), "/graph");
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
        server.start();
    }
}