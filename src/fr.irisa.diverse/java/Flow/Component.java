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
    private JSONObject component = null;
    private String fromLibrary = "";
    private String name = "";
    private String description = "";
    private Ports inports = null;
    private Ports outports = null;
    private String language = "";
    private String code = "";
    private String tests = "";

    // Constructor
    public Component (JSONObject json, String library) {
        component = new JSONObject();
        this.fromLibrary = library;
        this.name = library + "/" + json.get("name");
        this.description = (String) json.get("description");

        if(json.get("langague") != null) this.language = (String) json.get("language");
        if(json.get("code") != null) this.code = (String) json.get("code");
        if(json.get("tests") != null) this.tests = (String) json.get("tests");
        if (json.get("inports") == null) inports = new Ports();
        else this.inports = buildPorts((String) json.get("inports"));

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

    public String getLanguage() { return language; }

    public String getCode() {
        return code;
    }

    public String getTests() {
        return tests;
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
