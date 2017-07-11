package fr.irisa.diverse.Core;

import fr.irisa.diverse.Webserver.Webserver;

import java.sql.Time;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

        // Configure behavior on SIGINT
        Runtime.getRuntime().addShutdownHook(SIGINTHandler());

        // Initialize and start webserver
        webserver = Webserver.getInstance();
        webserverThread = new Thread(webserver);
        webserverThread.start();

        // Initialize Root
        root = Root.getInstance();
        Map<String, Workspace> workspaces = root.getWorkspaces();

        Set<String> keys = workspaces.keySet();

    }

    /**
     * Stop all running kernels on SIGINT signal
     * @return : the thread stopping the containers
     */
    private static Thread SIGINTHandler () {

        return new Thread () {
            @Override
            public void run () {
                System.out.println("Shutting down all the kernels");

                // Retrieve the iterator for the workspaces
                Map<String, Workspace> workspacesMap = root.getWorkspaces();
                Collection<Workspace> workspaces = workspacesMap.values();
                Iterator<Workspace> iterator = workspaces.iterator();

                // Save, and stop the kernels for each workspace
                // Use an executor service in order to send the stop signal to the kernels in as few time as possible
                // and wait for every one of them to be stopped before finishing this program.
                ExecutorService es = Executors.newCachedThreadPool();

                while (iterator.hasNext()) {
                    Workspace w = iterator.next();

                    Runnable task = () -> {
                        // w.save(); NOT IN DEVELOPMENT
                        try {
                            w.stopKernels();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    };

                   es.execute(task);
                }

                try {
                    es.shutdown();
                    es.awaitTermination(3, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
