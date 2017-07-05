package fr.irisa.diverse.Core;

import fr.irisa.diverse.Webserver.Webserver;

import java.util.Map;
import java.util.Set;

/**
 * Unique main of the project.
 *
 * Its role is to initialize the project. It creates a Root instance and start the webserver.
 *
 * Created by antoine on 06/06/17.
 */
public class Main {

    // Attributes
    private static Root root;
    private static Webserver webserver;
    private static Thread webserverThread;

    public static void main (String[] args) throws Exception {

        // Initialize and start webserver
        webserver = Webserver.getInstance();
        webserverThread = new Thread(webserver);
        webserverThread.start();

        // Initialize Root
        root = Root.getInstance();
        Map<String, Workspace> workspaces = root.getWorkspaces();

        Set<String> keys = workspaces.keySet();

    }
}
