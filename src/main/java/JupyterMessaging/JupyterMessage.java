package JupyterMessaging;

import Core.Kernel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Implementation of the basis of a Jupyter message.
 * The documentation is available here : http://jupyter-client.readthedocs.io/en/latest/messaging.html#general-message-format
 *
 * Created by antoine on 10/05/2017.
 */
public class JupyterMessage {

    // Attributes
    private Kernel kernel = null;
    private JSONObject message;

    // Main elements of the message
    private String uuid;
    private String hmac;
    private String delimiter;
    private JSONObject header;
    private JSONObject parent_header;
    private JSONObject metadata;
    private JSONObject content;

    /**
     * Constructor with minimal number of arguments
     */
    public JupyterMessage(Kernel kernel, String msg_type) {
        this.kernel = kernel;

        String msg_id = UUID.randomUUID().toString();
        String username = kernel.getContainerId();
        String session = kernel.getSession();
        String date = generateDate(); // ISO 8061 compliant timestamp
        uuid = "";
        delimiter = "<IDS|MSG>";

        // Build the header
        header = new JSONObject();
        header.put("msg_id", msg_id);
        header.put("username", username);
        header.put("session", session);
        header.put("date", date);
        header.put("msg_type", msg_type);
        header.put("version", "5.1");

        // Initialize other attributes
        parent_header = new JSONObject();
        metadata = new JSONObject();
        content = new JSONObject();
    }

    /**
     * Complete constructor for message to send
     * @param kernel : source kernel
     * @param msg_type : the type of message to send
     * @param parent_header :  dict
     * @param metadata :  dict
     * @param content : dict
     */
    public JupyterMessage(Kernel kernel, String msg_type, JSONObject parent_header, JSONObject metadata, JSONObject content) {
        this(kernel, msg_type);
        if (parent_header != null) this.parent_header = parent_header;
        if (metadata != null) this.metadata = metadata;
        if (content != null) this.content = content;
    }

    /** Constructor for incoming messages
     *
     * @param incomingMessage : Array of String containing the parts of the message
     */
    public JupyterMessage(Kernel kernel, String[] incomingMessage) {
        // Store the source kernel instance
        this.kernel = kernel;

        // Create the message from the received data
        message = new JSONObject();

        if(incomingMessage.length == 7) {
            message.put("uuid", incomingMessage[0]);
            this.uuid = incomingMessage[0];
            message.put("delimiter", incomingMessage[1]);
            this.delimiter = incomingMessage[1];
            message.put("hmac", incomingMessage[2]);
            this.hmac = incomingMessage[2];
            message.put("header", incomingMessage[3]);
            message.put("parent_header", incomingMessage[4]);
            message.put("metadata", incomingMessage[5]);
            message.put("content", incomingMessage[6]);

            // Construct the header, parent_header, metadata and content to make them easily accessible
            JSONParser jsonParser = new JSONParser();
            try {
                header = (JSONObject) jsonParser.parse(incomingMessage[3]);
                parent_header = (JSONObject) jsonParser.parse(incomingMessage[4]);
                metadata = (JSONObject) jsonParser.parse(incomingMessage[5]);
                content = (JSONObject) jsonParser.parse(incomingMessage[6]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Incoming message error : missing informations");
        }

    }

    /* =================================================================================================================
                                               GETTER AND SETTERS
     =================================================================================================================*/

    public JSONObject getHeader () { return this.header; };

    public void setParentHeader (JSONObject parent_header) {
        this.parent_header = parent_header;
    }

    public JSONObject getParentHeader () { return this.parent_header; }

    public void setMetadata (JSONObject metadata) {
        this.metadata = metadata;
    }

    public String getHmac () {
        if(this.hmac == null) this.hmac = generateHmac();
        return this.hmac;
    }

    public JSONObject getMetadata () { return this.metadata; }

    public String getUuid () { return uuid; }

    public void setContent (JSONObject content) {
        this.content = content;
    }

    public JSONObject getContent () { return this.content; }

    public String[] getMessageToSend () {
        buildMessage();

        // Add each field in the right order and respect the way python list are built
        String[] msg = new String[6];
        msg[0] = this.message.get("delimiter").toString();
        msg[1] = this.message.get("hmac").toString();
        msg[2] = this.message.get("header").toString();
        msg[3] = this.message.get("parent_header").toString();
        msg[4] = this.message.get("metadata").toString();
        msg[5] = this.message.get("content").toString();

        return msg;
    }

    /* =================================================================================================================
                                                CUSTOM METHODS
     =================================================================================================================*/

    /**
     * Generate an ISO 8061 compliant timestamp
     * @return : String - the timestamp
     */
    private String generateDate () {
        TimeZone tz = TimeZone.getTimeZone("GMT+1");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.ssssss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        return df.format(new Date());
    }

    /**
     * Generate the Jupypter messaging protocol compliant hmac
     * @return
     */
    private String generateHmac () {
        // TODO : PRIORITY
        final String ALGORITHM = "HmacSHA256";

        String result = "";

        try {
            Mac hmac = Mac.getInstance(ALGORITHM);
            SecretKeySpec sk = new SecretKeySpec(kernel.getKey().getBytes(), ALGORITHM);
            hmac.init(sk);
            hmac.update(header.toString().getBytes());
            hmac.update(parent_header.toString().getBytes());
            hmac.update(metadata.toString().getBytes());
            hmac.update(content.toString().getBytes());
            byte[] mac_data = hmac.doFinal();

            // Convert the hmac into a String to send it
            for (final byte element : mac_data)
            {
                result += Integer.toString((element & 0xff) + 0x100, 16).substring(1);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Build the message in accordance with Jupyter messaging specification
     */
    private void buildMessage () {
        // Build the message
        message = new JSONObject();
        message.put("uuid", uuid);
        message.put("delimiter", delimiter);
        message.put("header", header);

        message.put("parent_header", parent_header.toString());
        message.put("metadata", metadata.toString());
        message.put("content", content.toString());

        // Generate and add the hmac
        hmac = generateHmac();
        message.put("hmac", hmac);
    }

}
