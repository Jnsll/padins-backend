package fr.irisa.diverse.Core;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by antoine on 25/05/2017.
 */
public class Root {
    // Attributes
    private Map<String, Workspace> workspaces = null;


    // Singleton object
    private static Root ourInstance = new Root();

    // Singleton specific getInstance method
    public static Root getInstance() {
        return ourInstance;
    }

    // Constructor
    private Root() {
        workspaces = new Hashtable<>();
    }

    /*==================================================================================================================
                                              PUBLIC CLASS METHODS
     =================================================================================================================*/

    public String createWorkspace (String name, int port) {
        Workspace newWorkspace = new Workspace(name, port);

        workspaces.put(newWorkspace.uuid, newWorkspace);

        return newWorkspace.uuid;
    }

    public void deleteWorkspace (String uuid) {
        workspaces.remove(uuid);
    }
}
