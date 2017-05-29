package Flow;

import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by antoine on 29/05/17.
 */
public class Ports extends ArrayList<Port> {

    // Attributes
    ArrayList<Port> ports = null;

    public Ports () {
        ports = new ArrayList<Port>();
    }

    @Override
    public String toString () {
        JSONObject res = new JSONObject();

        for (int i=0; i<ports.size(); i++) {
            res.put(i, ports.get(i).toString());
        }

        return res.toJSONString();
    }
}
