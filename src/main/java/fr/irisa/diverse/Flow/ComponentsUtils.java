package fr.irisa.diverse.Flow;

import fr.irisa.diverse.Core.Root;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A Utils class to make easier retrieving components and their inports and outports.
 *
 * Created by antoine on 29/05/17.
 */
public class ComponentsUtils {

    // Attributes
    private static final String pathToComponentLibrary = Root.PATH_TO_PROJECT_STORAGE + "/WebUIComponents/";
    private static ArrayList<Component> components = null;
    private static final JSONParser parser = new JSONParser();
    private static String lastRequestedLibrary = "";

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    /**
     * Give all the inports given the name of a component
     * @param component : the component for whom you want its inports
     * @return : An arraylist containing all inports
     */
    public static Ports getInPortsForComponent(String library, String component, String node) {
        Ports inports = getComponent(library, component).getInports();
        inports.setNode(node);
        return inports;
    }

    /**
     * Give all the outports given the name of a component
     * @param component : the component for whom you want its inports
     * @return : An arraylist containing all inports
     */
    public static Ports getOutPortsForComponent(String library, String component, String node) {
        Ports outports = getComponent(library, component).getOutports();
        outports.setNode(node);
        return outports;
    }

    /* =================================================================================================================
                                                    PRIVATE FUNCTIONS
       ===============================================================================================================*/

    /**
     * Retrieve all the components for a given library
     *
     * @param library The library of components
     * @return The list of components
     */
    public static ArrayList<Component> getComponentsFromLib (String library) {
        // Initialize components
        components = new ArrayList<>();

        try {
            lastRequestedLibrary = library;
            // Read and parse the JSON file
            Object obj = parser.parse(new FileReader(pathToComponentLibrary + library + ".json"));
            JSONObject componentsObj = (JSONObject) obj;
            JSONArray componentsArray = (JSONArray) componentsObj.get("components");

            // Now create a component object for each element of the jsonObject
            for(int i=0; i<componentsArray.size(); i++) {
                JSONObject object = (JSONObject) componentsArray.get(i);

                components.add(new Component(object, library));
            }

            // Return this list after adding each found component
            return components;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        // Just in case we didn't find anything
        return new ArrayList<Component>();
    }

    /**
     * Retrieve a specific component for a given name and library
     *
     * @param library The library of components
     * @param name The search component's name
     * @return The component requested if existing. Otherwise null.
     */
    public static Component getComponent(String library, String name) {
        // If the last request library is not the same as the one requested this time, we load the
        // data from the corresponding JSON file which can be found in ressources/WebUIComponents
        if (!library.equals(lastRequestedLibrary)) components = getComponentsFromLib(library);

        // Go through the components to find and return the requested one
        for (Component component : components) {
            if (component.getName().equals(library + "/" + name) || component.getName().equals(library + "\\/" + name)) return component;
        }

        return null; // if not found
    }


}
