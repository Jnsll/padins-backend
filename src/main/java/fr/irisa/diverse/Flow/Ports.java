package fr.irisa.diverse.Flow;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by antoine on 29/05/17.
 */
public class Ports extends ArrayList<Port> {

    // Attributes
    private ArrayList<Port> ports = null;
    private JSONArray portsJson = null;

    // Constructors
    public Ports () {
        ports = new ArrayList<Port>();
        portsJson = new JSONArray();
    }

    public Ports (JSONArray array) {
        this();

        for (Object obj: array) {
            if (obj instanceof JSONObject) {
                // Retrieve needed information to create a new Port instance
                JSONObject object = (JSONObject) obj;
                String name = object.get("name") != null ? (String) object.get("name") : "";
                String port = object.get("port") != null ? (String) object.get("port") : "";
                JSONObject metadata = object.get("metadata") != null ? (JSONObject) object.get("port") : null;

                // Add the port into the Ports object
                if (metadata == null) ports.add(new Port(port, name));
                else ports.add(new Port(port, name, metadata));
            }
        }

        // Finally build the JSON
        build();
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    @Override
    public String toString () {
        build();
        return portsJson.toJSONString();
    }

    @Override
    public int size () {
        return ports.size();
    }

    public JSONArray toJson () {
        build();
        return portsJson;
    }

    @Override
    public Port get (int index) {
        return ports.get(index);
    }

    public void setNode (String node) {
        for (Object o: ports) {
            Port port = (Port) o;
            port.setNode(node);
        }
    }

    public int indexOfPort (String name) {
        for(int i=0; i<ports.size(); i++) {
            if (ports.get(i).getName().equals(name)) return i;
        }

        return -1;
    }

    /* =================================================================================================================
                                                    PRIVATE FUNCTIONS
       ===============================================================================================================*/

    private void build () {
        portsJson = new JSONArray();
        for (Object port: ports) {
            Port p = (Port) port;
            portsJson.add(p.toJson());
        }
    }
}
