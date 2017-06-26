package fr.irisa.diverse.Webserver.Servlets;

import fr.irisa.diverse.Core.Root;
import fr.irisa.diverse.Core.Workspace;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;

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
                        response.addHeader("Access-Control-Allow-Origin", "*"); // TODO remove in prod mode
                        System.out.println("Set response header");
                        //  writer.println("New file " + file.getSubmittedFileName() + " created at " + path);
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

    /* =================================================================================================================
                                                  UTILS METHODS
       ===============================================================================================================*/

    private boolean pathIsValid (String path) {
        // TODO : write the method that verify that the path is in the workspace's folder and not above it,
        // to avoid security issues
        return true;
    }
}
