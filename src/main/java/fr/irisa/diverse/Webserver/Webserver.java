package fr.irisa.diverse.Webserver;

import fr.irisa.diverse.Webserver.Servlets.WebsocketServlet;
import fr.irisa.diverse.Webserver.Servlets.WorkspacesServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * A REST webserver that serves the static content and provides methods to discover the available workspaces.
 * After the client choose the workspace she wants to connect to, the communications switch to the websocket.
 *
 * SINGLETON
 *
 * Created by antoine on 06/06/17.
 */
public class Webserver implements Runnable {

    private static Webserver instance = null;

    private final String SERVER_IP = "0.0.0.0";
    private final int SERVER_PORT = 8080;

    // Singleton methods
    public static Webserver getInstance () {
        if (instance == null) instance = new Webserver();

        return instance;
    }

    // Constructor
    private Webserver () {
        // Do nothing
    }

    public void run () {
        // Define the server
        Server server = new Server();

        // Add http connector to server
        ServerConnector http = new ServerConnector(server);
        http.setHost(SERVER_IP);
        http.setPort(SERVER_PORT);
        http.setIdleTimeout(30000);
        server.addConnector(http);

        // Create contextHandlers that serves the static content (html, css and js files) and configures it

        ContextHandler contextHandler1 = new ContextHandler("/");
        contextHandler1.setResourceBase("src/main/webapp/src");
        contextHandler1.setWelcomeFiles(new String[]{"index.html"});
        contextHandler1.setHandler(new ResourceHandler());

        ContextHandler node_modules =  new ContextHandler("/node_modules/");
        node_modules.setResourceBase("src/main/webapp/node_modules");
        node_modules.setHandler(new ResourceHandler());

        // Create a servlet context handler
        ServletContextHandler servlets = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servlets.setContextPath("/API");
        servlets.addServlet(new ServletHolder(new WorkspacesServlet()), "/workspaces/*");

        // Create a websocket servlet handler
        ServletContextHandler socket = new ServletContextHandler(ServletContextHandler.SESSIONS);
        socket.addServlet(new ServletHolder(new WebsocketServlet()), "/ws");

        // Add the contextHandler and the servlet context handler to the server
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{ contextHandler1, node_modules, servlets, socket});
        server.setHandler(handlers);

        // Start the server (because it is configured :) )
        // Server.join is used to make the server join the current thread
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
