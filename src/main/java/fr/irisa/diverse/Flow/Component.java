package fr.irisa.diverse.Flow;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A component is an element that turns to be a Node we used on the UI.
 * It is something close to the notion of Class in object-oriented programming. It can be used to do a Node but not
 * alone.
 *
 * A component is a concept of Flow-Based Programming.
 *
 * There is a library that contains all the component a given workspace can use.
 *
 * Created by antoine on 29/05/17.
 */
public class Component {

    // Attributes
    private JSONObject component = null;
    private String fromLibrary = "";
    private String name = "";
    private String description = "";
    private JSONArray inports;
    private JSONArray outports;
    private String language = "";
    private String code = "";
    private String tests = "";
    private boolean executable;

    private JSONObject originalJsonObject;

    /* =================================================================================================================
                                                    CONSTRUCTOR
       ===============================================================================================================*/
    Component (JSONObject json, String library) {
        this.originalJsonObject = json;
        component = new JSONObject();
        this.fromLibrary = library;
        this.name = library + "/" + json.get("name");
        this.description = (String) json.get("description");

        if(json.get("langague") != null) this.language = (String) json.get("language");
        if(json.get("code") != null) this.code = (String) json.get("code");
        if(json.get("tests") != null) this.tests = (String) json.get("tests");
        this.executable = json.get("executable") != null && (boolean) json.get("executable");
        if(json.get("inports") == null || !(json.get("inports") instanceof JSONArray)) inports = new JSONArray();
        else {
            this.inports = (JSONArray) json.get("inports");
        }

        if (json.get("outports") == null || !(json.get("outports") instanceof JSONArray)) outports = new JSONArray();
        else {
            this.outports = (JSONArray) json.get("outports");
        }
    }

    /* =================================================================================================================
                                                    GETTERS AND SETTERS
       ===============================================================================================================*/

    /**
     * Gives the name of the component
     * @return name as String
     */
    public String getName () {
        return name;
    }

    /**
     * Gives the description of a Component. Usually what it's made for.
     *
     * @return the description as String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gives the list of inports the Component has.
     * The inports are ports to connect to when on the graph.
     *
     * @return the list of inports
     */
    Ports getInports() {
        return new Ports(inports);
    }

    /**
     * Gives the list of outports the Component has.
     * The outports are ports to send data to an other node's inport.
     *
     * @return the list of outports
     */
    Ports getOutports() {
        return new Ports(outports);
    }

    /**
     * Gives the programming language used by the component
     *
     * @return the programming language as String
     */
    public String getLanguage() {
        if(language == null) language = "";
        return language;
    }

    /**
     * Gives the source code of the component
     *
     * @return the source code as String
     */
    public String getCode() {
        return code;
    }

    /**
     * Gives the tests code of the component
     *
     * @return the tests code as String
     */
    public String getTests() {
        return tests;
    }

    /**
     * Whether the component is executable or not
     *
     * @return true if executable
     */
    public boolean isExecutable() {
        return executable;
    }

    /* =================================================================================================================
                                                    PRIVATE FUNCTIONS
       ===============================================================================================================*/

    /**
     * Build the JSON object component with up-to-date information.
     * Usually used right before serializing the message.
     */
    private void buildJson () {
        // Build the component JSON
        component.put("name", getName());
        component.put("description", getDescription());
        component.put("icon", "");
        component.put("subgraph", false); // TODO
        component.put("inPorts", getInports().toString());
        component.put("outPorts", getOutports().toString());
        component.put("code", code);
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    /**
     * Override the very common toString function that return a String representing the object.
     *
     * @return the component serialized
     */
    @Override
    public String toString () {
        buildJson();
        // Return it as a String
        return component.toJSONString();
    }

    /**
     * Give the JSON containing all the information about the component
     *
     * @return the JSON as JSONObject
     */
    public JSONObject toJson() {
        buildJson();
        return new JSONObject(component);
    }
}
