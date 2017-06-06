package fr.irisa.diverse.Core;

import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

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

        loadStoredWorkspaces();
    }

    /*==================================================================================================================
                                              PUBLIC CLASS METHODS
     =================================================================================================================*/

    /**
     * Create a new workspace. A workspace approximately corresponds to one project on the IDE.
     *
     * @param name of the workspace/project
     * @return the uuid of the newly create workspace
     */
    public String createWorkspace (String name) {
        // Create a new workspace with the given name
        Workspace newWorkspace = new Workspace(name);

        // Store the workspace, associated with its uuid in a Map
        workspaces.put(newWorkspace.uuid, newWorkspace);

        // Return the uuid
        return newWorkspace.uuid;
    }

    /**
     * Delete a workspace based on its uuid.
     * Be careful with this method ! It will remove all the files in the project, the flow, etc... and it cannot
     * be undone
     *
     * @param uuid : the id of the workspace to delete
     * @return True if the workspace has been deleted
     */
    public boolean deleteWorkspace (String uuid, String name) {
        if (workspaces.containsKey(uuid) && workspaces.get(uuid).getName().equals(name)) {
            workspaces.remove(uuid);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tell whether the given workspace exist
     *
     * @param workspace The ID of the workspace
     * @return True if the workspace exists
     */
    public boolean hasWorkspace (String workspace) {
        return workspaces.containsKey(workspace);
    }

    /**
     * Give the requested workspace
     *
     * @param id The ID of the request workspace
     * @return The workspace instance
     */
    public Workspace getWorkspace (String id) {
        if (hasWorkspace(id)) {
            return workspaces.get(id);
        } else {
            return null;
        }
    }

    /*==================================================================================================================
                                                SETTERS AND GETTERS
     =================================================================================================================*/

    public Map<String, Workspace> getWorkspaces() {
        return workspaces;
    }

    /*==================================================================================================================
                                              PRIVATE CLASS METHODS
     =================================================================================================================*/

    private void loadStoredWorkspaces () {
        // TODO
    }


}
