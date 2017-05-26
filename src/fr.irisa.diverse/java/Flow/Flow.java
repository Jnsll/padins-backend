package Flow;

import Core.Workspace;
import org.json.simple.JSONObject;

/** A flow is the JSON file containing all the data structure of a workspace.
 * The web interface uses it, and only it, to create the view.
 *
 * Created by antoine on 26/05/2017.
 */
public class Flow {

    // Attributes
    JSONObject flow = null;
    Workspace owningWorkspace = null;

    // Constructor
    public Flow (Workspace workspace) {
        this.owningWorkspace = workspace;
        this.flow = new JSONObject();
    }
}
