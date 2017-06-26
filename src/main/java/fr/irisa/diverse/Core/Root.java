package fr.irisa.diverse.Core;

import fr.irisa.diverse.Utils.Utils;

import java.io.File;
import java.util.*;

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
    public final String PATH_TO_WORKSPACE_STORAGE = Root.class.getClassLoader().getResource("workspaces/").getPath();

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
        Workspace newWorkspace = new Workspace(name, null);

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
            // TODO : remove the folder
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
        loadStoredWorkspaces();

        Utils.wait(50);

        return workspaces;
    }

    /*==================================================================================================================
                                              PRIVATE CLASS METHODS
     =================================================================================================================*/

    private void importWorkspace (String uuid) {
        // Verify that the workspace doesn't already exist
        if (workspaces.get(uuid) == null) {
            // Create a new workspace with the given name
            Workspace newWorkspace = new Workspace(null, uuid);

            // Store the workspace, associated with its uuid in a Map
            workspaces.put(newWorkspace.uuid, newWorkspace);
        }
    }

    private void loadStoredWorkspaces () {
        final String workspacesPath = Root.class.getClassLoader().getResource("workspaces/").getPath();
        // First : load the list of folder in the ressources/workspaces folder
        File dir = new File(workspacesPath);
        File[] files = dir.listFiles();

        // Add all the directories name into a List
        List<String> workspacesNames = new ArrayList<>();
        for (File f : files) {
            if(f.isDirectory() ) workspacesNames.add(f.getName());
        }

        workspacesNames.remove("utils");

        // Create a workspace for each directory found
        for (String uuid : workspacesNames) {
            importWorkspace(uuid);
        }
    }


}
