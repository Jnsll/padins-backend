package Flow;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by antoine on 29/05/17.
 */
public class Component {

    // Attributes
    JSONObject component = null;
    String name = "";
    String description = "";
    Ports inports = null;
    Ports outports = null;

    // Constructor
    public Component (JSONObject json) {
        component = new JSONObject();
        this.name = (String) json.get("name"); // TODO : add project name as a prefix
        this.description = (String) json.get("description");

        if (json.get("inports") == null) inports = new Ports();
        this.inports = buildPorts((String) json.get("inports"));

        if (json.get("outports") == null) outports = new Ports();
        else this.outports = buildPorts((String) json.get("outports"));
    }

    /* =================================================================================================================
                                                    GETTERS AND SETTERS
       ===============================================================================================================*/

    public String getName () {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Ports getInports() {
        return inports;
    }

    public Ports getOutports() {
        return outports;
    }

    /* =================================================================================================================
                                                    PRIVATE FUNCTIONS
       ===============================================================================================================*/

    private Ports buildPorts (String json) {
        JSONParser parser = new JSONParser();
        JSONObject ports;
        Ports portsToReturn = new Ports();

        try {
            // Parse the json containing inputs to a JSONObject we can manipulate
            ports = (JSONObject) parser.parse(json);

            // Go trough the JSON to retrieve all the information we need
            Set keys = ports.keySet();
            Iterator iterator = keys.iterator();
            while(iterator.hasNext()) {
                // For each object, create an instance of Port and add it to the portsToReturn object
                JSONObject tempJSONPort = (JSONObject) ports.get(iterator.next());
                Port tempPort = new Port((String) tempJSONPort.get("port"));
                portsToReturn.add(tempPort);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return portsToReturn;
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    @Override
    public String toString () {
        // Build the edge JSON
        component.put("name", getName());
        component.put("description", getDescription());
        component.put("icon", "");
        component.put("subgraph", false); // TODO
        component.put("inPorts", getInports().toString());
        component.put("outPorts", getOutports().toString());

        // Return it as a String
        return component.toJSONString();
    }
}
