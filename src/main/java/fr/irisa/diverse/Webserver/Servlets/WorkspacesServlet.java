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
 * Provides a RESTFul service to manage the workspaces.
 *
 * A workspace correspond to a project. Each workspace has one (work)flow/graph and an associated directory to let the
 * user import all the files she needs.
 *
 * Implements : GET, PUT, POST, DELETE
 *
 * Created by antoine on 06/06/17.
 */
@SuppressWarnings("unchecked")
public class WorkspacesServlet extends HttpServlet{

    // Attributes
    private Root root;

    /* =================================================================================================================
                                                      CONSTRUCTOR
       ===============================================================================================================*/

    public WorkspacesServlet() {
        root = Root.getInstance();
    }

    /* =================================================================================================================
                                                  HTTPSERVLET METHODS
       ===============================================================================================================*/

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve the list of available workspaces
        JSONArray workspacesList = getWorkspacesList();

        // Set the response header, telling the request is ok and we will return a JSON
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.addHeader("Access-Control-Allow-Origin", "*");

        // Send the list of workspaces
        response.getWriter().println(workspacesList.toJSONString());
    }

    @Override
    public void doPut (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve the parameter name
        String name = request.getParameter("name");

        if (name != null) {

            // Create a workspace
            root.createWorkspace(name);

            // Set the response status to OK
            response.setStatus(HttpServletResponse.SC_OK);
            response.addHeader("Access-Control-Allow-Origin", "*");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            response.addHeader("Access-Control-Allow-Origin", "*");
        } else {
            // It means that a problem occurs or the workspace doesn't exist
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    public void doDelete (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve the name and id of the workspace to delete
        String uuid = request.getParameter("uuid");
        String name = request.getParameter("name");

        if (root.deleteWorkspace(uuid, name)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.addHeader("Access-Control-Allow-Origin", "*");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    @Override
    public void doOptions (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*"); // TODO remove in prod mode
        response.addHeader("Access-Control-Allow-Methods", "POST, DELETE, PUT");
    }

    /* =================================================================================================================
                                                  CUSTOM METHODS
       ===============================================================================================================*/

    /**
     * Returns the list of existing workspaces
     *
     * @return {ArrayList} the list of existing workspaces
     */
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
