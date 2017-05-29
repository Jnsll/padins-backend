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

    public static ArrayList<Component> getComponentsFromLib () {
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
