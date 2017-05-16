package JupyterMessaging;

import Core.Kernel;
import org.json.simple.JSONObject;

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
public class BaseMessage {

    // Attributes
    private Kernel kernel = null;
    private JSONObject message;

    // Main elements of the message
    private String identity;
    private String hmac;
    private JSONObject header;
    private JSONObject parent_header;
    private JSONObject metadata;
    private JSONObject content;

    /**
     * Constructor with minimal number of arguments
     */
    public BaseMessage (Kernel kernel, String msg_type) {
        this.kernel = kernel;

        String msg_id = UUID.randomUUID().toString();
        String username = kernel.getContainerId();
        String session = kernel.getSession();
        String date = generateDate(); // ISO 8061 compliant timestamp
        identity = "server." + kernel.getIdentity() + "." + msg_type;

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

    public BaseMessage (Kernel kernel, String msg_type, JSONObject parent_header, JSONObject metadata, JSONObject content) {
        this(kernel, msg_type);
        if (parent_header != null) this.parent_header = parent_header;
        if (metadata != null) this.metadata = metadata;
        if (content != null) this.content = content;
    }

    /* =================================================================================================================
                                               GETTER AND SETTERS
     =================================================================================================================*/

    public void setParentHeader (JSONObject parent_header) {
        this.parent_header = parent_header;
    }

    public void setMetadata (JSONObject metadata) {
        this.metadata = metadata;
    }

    public void setContent (JSONObject content) {
        this.content = content;
    }

    public String[] getMessageToSend () {
        buildMessage();

        String[] message = new String[7];
        message[0] = this.message.get("uuid").toString();
        message[1] = this.message.get("delimiter").toString();
        message[2] = this.message.get("hmac").toString();
        message[3] = this.message.get("header").toString();
        message[4] = this.message.get("parent_header").toString();
        message[5] = this.message.get("metadata").toString();
        message[6] = this.message.get("content").toString();

        return message;
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
        final String ALGORITHM = "HmacSHA256";

        String result = "";

        try {
            Mac hmac = Mac.getInstance(ALGORITHM);
            SecretKeySpec sk = new SecretKeySpec(kernel.getKey().getBytes(), ALGORITHM);
            hmac.init(sk);
            hmac.update(parent_header.toString().getBytes());
            hmac.update(metadata.toString().getBytes());
            hmac.update(content.toString().getBytes());
            byte[] mac_data = hmac.doFinal();
            for (final byte element : mac_data) {
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
        message.put("uuid", identity);
        message.put("delimiter", "<IDS|MSG>");
        message.put("header", header);

        message.put("parent_header", parent_header.toString());
        message.put("metadata", metadata.toString());
        message.put("content", content.toString());

        // Generate and add the hmac
        hmac = generateHmac();
        message.put("hmac", hmac);
    }

}
