package fr.irisa.diverse.Webserver.Servlets;

import fr.irisa.diverse.Core.Root;
import fr.irisa.diverse.Core.Workspace;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by antoine on 26/06/17.
 */
public class UploadServlet  extends HttpServlet {

    // Attributes
    private Root root;
    private Workspace linkedWorkspace;

    // Constructor
    public UploadServlet () {
        // Retrieve the root element
        this.root = Root.getInstance();
    }

    /* =================================================================================================================
                                                  HTTP METHODS
       ===============================================================================================================*/

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Retrieve the id of the workspace
        String id = request.getParameter("workspace");

        if (id != null) {
            linkedWorkspace = root.getWorkspace(id);

            if (linkedWorkspace != null) {
                response.addHeader("Access-Control-Allow-Origin", "*"); // TODO remove in prod mode
                // Retrieve the file and the path
                final String path = linkedWorkspace.getPathToWorkspaceFolder().toString() + "/" + request.getParameter("path");
                final Part file = request.getPart("file");

                OutputStream out = null;
                InputStream filecontent = null;
                final PrintWriter writer = response.getWriter();
                if (pathIsValid(path)) {
                    try {
                        out = new FileOutputStream(new File(path + file.getSubmittedFileName()));
                        filecontent = file.getInputStream();

                        int read = 0;
                        final byte[] bytes = new byte[1024];

                        while ((read = filecontent.read(bytes)) != -1) {
                            out.write(bytes, 0, read);
                        }

                        // Set the status to tell the client everything went well
                        response.setStatus(HttpServletResponse.SC_OK);
                    } catch (FileNotFoundException fne) {
                        fne.printStackTrace();
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    } finally {
                        if (out != null) {
                            out.close();
                        }
                        if (filecontent != null) {
                            filecontent.close();
                        }
                        if (writer != null) {
                            writer.close();
                        }
                    }
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    public void doOptions (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*"); // TODO remove in prod mode
        response.addHeader("Access-Control-Allow-Methods", "POST, DELETE, PUT");
    }

    @Override
    public void doDelete (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*"); // TODO remove in prod mode
        // Retrieve the id of the workspace and the path
        String id = request.getParameter("workspace");
        String p = request.getParameter("path");

        // Verify that the request has been correctly formatted and that the workspace exist
        if (id != null && p != null && root.getWorkspace(id) != null) {
            // Then retrieve the workspace instance and the absolute path to the file to delete
            linkedWorkspace = root.getWorkspace(id);
            final String path = linkedWorkspace.getPathToWorkspaceFolder().toString() + "/" + request.getParameter("path");

            // Try to delete the file
            if (pathIsValid(path)) {
                File f = new File(path);
                if (f.exists()) {
                    // If it exist, we can delete. So, we do it and return a 200 status code
                    f.delete();
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    // Otherwise the path to the file was probably wrong, we send a bad_request status code
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else {
                // Otherwise the path to the file was probably wrong, we send a bad_request status code
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    public void doPut (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*"); // TODO remove in prod mode

        // Retrieve the id of the workspace, the name of the folder and the path
        String id = request.getParameter("workspace");
        String p = request.getParameter("path");
        String name = request.getParameter("name");

        // Verify that the request has been correctly formatted and that the workspace exist
        if (id != null && p != null && name != null && root.getWorkspace(id) != null) {
            // Then retrieve the workspace instance and the absolute path to the file to delete
            linkedWorkspace = root.getWorkspace(id);
            final String path = linkedWorkspace.getPathToWorkspaceFolder().toString() + "/" + p + name;

            if (pathIsValid(path) && Files.notExists(Paths.get(path))) {
                // Create the folder
                File f = new File(path);
                try {
                    f.mkdir();
                    response.setStatus(HttpServletResponse.SC_OK);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /* =================================================================================================================
                                                  UTILS METHODS
       ===============================================================================================================*/

    private boolean pathIsValid (String path) {
        // TODO : write the method that verify that the path is in the workspace's folder and not above it,
        // to avoid security issues
        return true;
    }
}
