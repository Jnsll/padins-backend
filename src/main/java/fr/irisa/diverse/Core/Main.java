package fr.irisa.diverse.Core;

import fr.irisa.diverse.Webserver.Webserver;

/**
 * Created by antoine on 06/06/17.
 */
public class Main {

    // Attributes
    private static Root root;
    private static Webserver webserver;

    public static void main (String[] args) throws Exception {

        // Initialize Root
        root = Root.getInstance();

        // Initialize and start webserver
        webserver = Webserver.getInstance();
        webserver.start();

        // Create a first workspace
        root.createWorkspace("Hillslope 1D");

    }
}