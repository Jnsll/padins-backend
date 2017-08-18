package fr.irisa.diverse.Core;

import fr.irisa.diverse.Utils.Unzipper;
import fr.irisa.diverse.Utils.Utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * This is the Root singleton of the project that loads the workspaces on startup and is used across the project
 * to manage the workspaces.
 *
 * Created by antoine on 25/05/2017.
 */
public class Root {
    // Attributes
    private Map<String, Workspace> workspaces = null;
    public static final String DATA_STRUCTURE_VERSION = "0-1-0";
    public static final String PATH_TO_PROJECT_STORAGE = "/usr/include/padins/" + DATA_STRUCTURE_VERSION;

    // Singleton object
    private static Root ourInstance = new Root();

    // Singleton specific getInstance method
    public static Root getInstance() {
        return ourInstance;
    }

    // Constructor
    private Root() {
        // Instantiate the workspaces Map
        workspaces = new Hashtable<>();

        // Verify that the folder where we store data in exists
        verifyStorageFolderExists();

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
    public void createWorkspace (String name) {
        // Create a new workspace with the given name
        Workspace newWorkspace = new Workspace(name, null);

        // Store the workspace, associated with its uuid in a Map
        workspaces.put(newWorkspace.uuid, newWorkspace);
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

    /**
     * @return the Map of Name <-> Workspace instance
     */
    public Map<String, Workspace> getWorkspaces() {
        if (workspaces != null && workspaces.isEmpty()) {
            loadStoredWorkspaces();
            Utils.wait(50);
        }

        return workspaces;
    }

    /*==================================================================================================================
                                              PRIVATE CLASS METHODS
     =================================================================================================================*/

    /**
     * Import  a workspace with the given uuid
     * @param uuid the unique id of the workspace to import.
     */
    private void importWorkspace (String uuid) {
        // Verify that the workspace doesn't already exist
        if (workspaces.get(uuid) == null) {
            // Create a new workspace with the given name
            Workspace newWorkspace = new Workspace(null, uuid);

            // Store the workspace, associated with its uuid in a Map
            workspaces.put(newWorkspace.uuid, newWorkspace);
        }
    }

    /**
     * Verify that the folder where we storage all the workspaces' data exsits.
     * If it doesn't, we create it.
     */
    private void verifyStorageFolderExists () {
        Path folderPath = Paths.get(PATH_TO_PROJECT_STORAGE);

        // Check whether the folder exists or not
        if (Files.notExists(folderPath)) {
            // If it doesn't exist we create it
            // 1. creating a File instance
            File folder = new File(PATH_TO_PROJECT_STORAGE);
            try {
                // 2. Using mkdir
                System.out.println("[INFO] Creating folder to : " + PATH_TO_PROJECT_STORAGE);
                folder.mkdir();
                System.out.println("[INFO] Successfully created folder " + PATH_TO_PROJECT_STORAGE);
                // Set add default_storage_directory_content to True
                addDefaultContent();
            } catch (Exception e) {
                // If something goes wrong we log it
                e.printStackTrace();
            }
        } else {
            // If the folder exists, we verify that it contains the version folder and the content of the
            // default_storage_directory_content that is in src/main/resources
            Path connexionFilesPath = Paths.get(PATH_TO_PROJECT_STORAGE + "/connexion_files");
            Path workspacesFilesPath = Paths.get(PATH_TO_PROJECT_STORAGE + "/workspaces");
            // Test whether they exist or not
            if (Files.notExists(connexionFilesPath) && Files.notExists(workspacesFilesPath)) {
                addDefaultContent();
            }
        }
    }

    /**
     * Add the default content that must contain the project storage folder into it.
     * This default content is in src/main/resources/default_storage_directory_content
     */
    private void addDefaultContent () {
        // 1. Retrieve the default_storage_directory_content.zip and copy
        // it into the newly created folder
        InputStream source = getClass().getClassLoader().getResourceAsStream("default_storage_directory_content.zip");
        String target = Root.PATH_TO_PROJECT_STORAGE;
        try {
            // 2. Unzip the file into the target directory
            Unzipper.unzip(source, target);
            // 3. Add execution permissions on all .sh files in the folder
            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "find " + Root.PATH_TO_PROJECT_STORAGE + "/ -type f -iname \"*.sh\" -exec chmod +x {} \\;");
            pb.directory(new File(target));
            pb.start();
            pb.redirectErrorStream(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 3. We are done !
    }

    /**
     * Load all the workspaces stored on the HD and store them in the
     * workspaces attribute of the class.
     */
    private void loadStoredWorkspaces () {
        if (workspaces == null) { workspaces = new HashMap<>(); }

        final String pathToWorkspacesStorage = Root.PATH_TO_PROJECT_STORAGE + "/workspaces";
        // First : load the list of folder in the $HOME/.padins/{version}/workspaces folder
        File dir = new File(pathToWorkspacesStorage);
        File[] files = dir.listFiles();

        if (files != null) {
            // Add all the directories name into a List
            List<String> workspacesNames = new ArrayList<>();
            for (File f : files) {
                if (f.isDirectory()) workspacesNames.add(f.getName());
            }

            workspacesNames.remove("utils");

            // Create a workspace for each directory found
            for (String uuid : workspacesNames) {
                importWorkspace(uuid);
            }
        }
    }


}
