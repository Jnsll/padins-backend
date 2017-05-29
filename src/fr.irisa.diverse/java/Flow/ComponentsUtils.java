package Flow;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by antoine on 29/05/17.
 */
public class ComponentsUtils {

    // Attributes
    private static String pathToComponentLibrary = ComponentsUtils.class.getResource("../WebUIComponents/example.json").getPath().replace("/example.json", "");
    private static ArrayList<Component> components = getComponentsFromLib();
    private static JSONParser parser = new JSONParser();
    private static String ONLY_EXISTING_LIBRARY = "hydro-geology.json";

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    /**
     * Give all the inports given the name of a component
     * @param component : the component for whom you want its inports
     * @return : An arraylist containing all inports
     */
    public static Ports getInPortsForComponent(String component) {
        return getComponent(component).getInports();
    }

    /**
     * Give all the outports given the name of a component
     * @param component : the component for whom you want its inports
     * @return : An arraylist containing all inports
     */
    public static Ports getOutPortsForComponent(String component) {
        return getComponent(component).getOutports();
    }

    /* =================================================================================================================
                                                    PRIVATE FUNCTIONS
       ===============================================================================================================*/

    private static ArrayList<Component> getComponentsFromLib () {
        try {
            // Read and parse the JSON file
            Object obj = parser.parse(new FileReader(pathToComponentLibrary + "/" + ONLY_EXISTING_LIBRARY)); // TODO modify to make it modular
            JSONObject jsonObject = (JSONObject) obj;
            jsonObject = (JSONObject) jsonObject.get("components");

            // Now create a component object for each element of the jsonObject
            for(int i=0; i<jsonObject.size(); i++) {
                JSONObject object = (JSONObject) jsonObject.get(i);

                components.add(new Component(object));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new ArrayList<Component>();
    }

    private static Component getComponent(String name) {
        for(int i=0; i<components.size(); i++) {
            if (components.get(i).getName().equals(name)) return components.get(i);
        }

        return null; // if not found
    }


}


class Component {

    // Attributes
    String name = "";
    String description = "";
    Ports inports = null;
    Ports outports = null;

    // Constructor
    public Component (JSONObject json) {
        this.name = (String) json.get("name");
        this.description = (String) json.get("description");

        if (json.get("inports") == null) inports = new Ports();
        this.inports = buildPorts((String) json.get("inports"));

        if (json.get("outports") == null) outports = new Ports();
        else this.outports = buildPorts((String) json.get("outports"));
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
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


}
