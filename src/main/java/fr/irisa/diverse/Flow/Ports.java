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

    private void build () {
        portsJson = new JSONArray();
        for (Object port: ports) {
            Port p = (Port) port;
            portsJson.add(p.toJson());
        }
    }
}
