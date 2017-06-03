package fr.irisa.diverse.Core;

import java.util.Hashtable;
import java.util.Map;

/**
 * This is the Root of the project that initialize everything in order to make this program running properly.
 *
 * To be sure there is only one Root we made it a singleton.
 *
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

    /**
     * Create a new workspace. A workspace approximately corresponds to one project on the IDE.
     *
     * @param name of the workspace/project
     * @param port on which the socket of the project should listen
     * @return the uuid of the newly create workspace
     */
    public String createWorkspace (String name, int port) {
        Workspace newWorkspace = new Workspace(name, port);

        workspaces.put(newWorkspace.uuid, newWorkspace);

        return newWorkspace.uuid;
    }

    /**
     * Delete a workspace based on its uuid.
     * Be careful with this method ! It will remove all the files in the project, the flow, etc... and it cannot
     * be undone
     *
     * @param uuid : the id of the workspace to delete
     */
    public void deleteWorkspace (String uuid) {
        workspaces.remove(uuid);
    }
}
