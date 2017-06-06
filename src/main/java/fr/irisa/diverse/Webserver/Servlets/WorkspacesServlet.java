package fr.irisa.diverse.Webserver.Servlets;

import fr.irisa.diverse.Core.Root;
import fr.irisa.diverse.Core.Workspace;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created by antoine on 06/06/17.
 */
public class WorkspacesServlet extends HttpServlet{

    // Attributes
    Root root;

    // Constructor
    public WorkspacesServlet() {
        root = Root.getInstance();
    }

    /* =================================================================================================================
                                                  HTTPSERVLET METHODS
       ===============================================================================================================*/

    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve the list of available workspaces
        JSONArray workspacesList = getWorkspacesList();

        // Set the response header, telling the request is ok and we will return a JSON
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        // Send the list of workspaces
        response.getWriter().println(workspacesList.toJSONString());
    }

    protected void doPut (HttpServletRequest request, HttpServletResponse response) {
        // Retrieve the parameter name
        String name = request.getParameter("name");

        // Create a workspace
        root.createWorkspace(name);

        // Set the response status to OK
        response.setStatus(HttpServletResponse.SC_OK);

    }

    protected void doPost (HttpServletRequest request, HttpServletResponse response) {
        // Retrieve the parameters name and uuid
        String uuid = request.getParameter("uuid");
        String name = request.getParameter("name");

        // Retrieve the workspace associated to the uuid
        Workspace w = root.getWorkspace(uuid);

        // Change the name with the one given in the request
        if (w != null) {
            w.setName(name);

            // Set the status of the response to OK
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            // It means that a problem occurs or the workspace doesn't exist
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected void doDelete (HttpServletRequest request, HttpServletResponse response) {
        // Retrieve the name and id of the workspace to delete
        String uuid = request.getParameter("uuid");
        String name = request.getParameter("name");

        if (root.deleteWorkspace(uuid, name)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    /* =================================================================================================================
                                                  CUSTOM METHODS
       ===============================================================================================================*/

    private JSONArray getWorkspacesList () {
        JSONArray res = new JSONArray();

        // Retrieve the list of workspaces
        Map<String, Workspace> workspaceMap = root.getWorkspaces();

        // Pure java code to be able to go trough the set (containing the list of workspaces)
        Set<String> keys = workspaceMap.keySet();

        // In the res array, add one object per workspace. This object contains the name and the id of the workspace.
        for (String key: keys) {
            JSONObject tempWorkspace = new JSONObject();
            tempWorkspace.put("name", workspaceMap.get(key).getName());
            tempWorkspace.put("uuid", key);

            res.add(tempWorkspace);
        }

        return res;
    }
}
