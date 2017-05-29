package Utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

/**
 * Created by antoine on 26/05/2017.
 */
public abstract class JSON {

    /**
     * Parse and return a JSONObject from a String
     * @param s : the string to parse
     * @return : a JSONObject that can be manipulate
     */
    public static JSONObject stringToJsonObject (String s) {
        JSONObject res = new JSONObject();

        JSONParser parser = new JSONParser();

        try {
            res = (JSONObject) parser.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return res;
    }

    public static String jsonArrayListToString (ArrayList list) {
        String res = "[";

        // Add each node's id into the array
        for(int i=0; i<list.size(); i++) {
            res += list.get(i).toString() + ",";
        }

        // Remove last unwanted coma
        res = res.substring(0, res.length()-1);

        res += "]";

        return res;
    }
}
