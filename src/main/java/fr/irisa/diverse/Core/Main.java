package fr.irisa.diverse.Core;

import fr.irisa.diverse.Webserver.Webserver;

import java.util.Map;
import java.util.Set;

/**
 * Created by antoine on 06/06/17.
 */
public class Main {

    // Attributes
    private static Root root;
    private static Webserver webserver;
    private static Thread webserverThread;

    public static void main (String[] args) throws Exception {

        // Initialize Root
        root = Root.getInstance();
        Map<String, Workspace> workspaces = root.getWorkspaces();

        Set<String> keys = workspaces.keySet();

        // Log the loaded workspaces
        for (String key : keys) {
            System.out.println(workspaces.get(key).getFlow().serialize());
        }

        // Initialize and start webserver
        webserver = Webserver.getInstance();
        webserverThread = new Thread(webserver);
        webserverThread.start();

    }
}
