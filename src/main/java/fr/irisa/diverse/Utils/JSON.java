package fr.irisa.diverse.Utils;

import fr.irisa.diverse.Flow.Edge;
import fr.irisa.diverse.Flow.Group;
import fr.irisa.diverse.Flow.Node;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

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
        for (Object aList : list) {
            res += aList.toString() + ",";
        }

        // Remove last unwanted coma
        res = res.substring(0, res.length()-1);

        res += "]";

        return res;
    }

    public static String[] jsonObjectToStringArray (JSONObject obj) {
        // Retrieve the iterator of the obj to go trough it.
        Set keys = obj.keySet();
        Iterator iterator = keys.iterator();
        // Create the String[] that will contain the result and be returned
        String[] res = new String[obj.size()];
        // A variable created only to help run the algorithm
        int i=0;

        // Go through the obj to put each object in the res array.
        while(iterator.hasNext()) {
            res[i] = (String) iterator.next();
            i++;
        }

        // Finished
        return res;
    }

    public static ArrayList<String> jsonObjectToArrayList (JSONObject obj) {
        // Retrieve the iterator of the obj to go trough it.
        Set keys = obj.keySet();
        Iterator iterator = keys.iterator();
        // Create the String[] that will contain the result and be returned
        ArrayList<String> res = new ArrayList<>();

        // Go through the obj to put each object in the res array.
        while(iterator.hasNext()) {
            res.add((String) iterator.next());
        }

        // Finished
        return res;
    }

    public static JSONArray jsonArrayFromArrayList (ArrayList list) {
        JSONArray res = new JSONArray();

        for (Object obj : list ) {
            if (obj instanceof Node) {
                Node n = (Node) obj;
                res.add(n.getJson());
            } else if (obj instanceof Edge) {
                Edge e = (Edge) obj;
                res.add(e.getJson());
            } else if (obj instanceof Group) {
                Group g = (Group) obj;
                res.add(g.getJson());
            } else {
                res.add(obj);
            }

        }

        return res;
    }
}
