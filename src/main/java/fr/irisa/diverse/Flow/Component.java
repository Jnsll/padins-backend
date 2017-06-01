package fr.irisa.diverse.Flow;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
    private Ports inports;
    private Ports outports;
    private String language = "";
    private String code = "";
    private String tests = "";
    private boolean executable;

    // Constructor
    public Component (JSONObject json, String library) {
        component = new JSONObject();
        this.fromLibrary = library;
        this.name = library + "/" + json.get("name");
        this.description = (String) json.get("description");

        if(json.get("langague") != null) this.language = (String) json.get("language");
        if(json.get("code") != null) this.code = (String) json.get("code");
        if(json.get("tests") != null) this.tests = (String) json.get("tests");
        this.executable = json.get("executable") != null && (boolean) json.get("executable");
        if(json.get("inports") == null || !(json.get("inports") instanceof JSONArray)) inports = new Ports();
        else {
            this.inports = new Ports((JSONArray) json.get("inports"));
        }

        if (json.get("outports") == null || !(json.get("outports") instanceof JSONArray)) outports = new Ports();
        else {
            this.outports = new Ports((JSONArray) json.get("outports"));
        }
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
        if (inports == null) inports = new Ports();
        return inports;
    }

    public Ports getOutports() {
        if (outports == null) outports = new Ports();
        return outports;
    }

    public String getLanguage() {
        if(language == null) language = "";
        return language;
    }

    public String getCode() {
        return code;
    }

    public String getTests() {
        return tests;
    }

    public boolean isExecutable() {
        return executable;
    }

    /* =================================================================================================================
                                                    PRIVATE FUNCTIONS
       ===============================================================================================================*/

    private void buildJson () {
        // Build the component JSON
        component.put("name", getName());
        component.put("description", getDescription());
        component.put("icon", "");
        component.put("subgraph", false); // TODO
        component.put("inPorts", getInports().toString());
        component.put("outPorts", getOutports().toString());
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    @Override
    public String toString () {
        buildJson();
        // Return it as a String
        return component.toJSONString();
    }

    public JSONObject toJson() {
        buildJson();
        return component;
    }
}
