import ZMQSockets.*;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by antoine on 28/04/17.
 */
public class Kernel {

    private Process container;
    private String containerId = null;
    private JSONParser parser = null;

    private String transport = null;
    private String ip = null;
    private String signature_scheme = null;
    private String key = null;
    private String pathToConnexionFiles = null;

    public JupyterChannel shell = null;
    public JupyterChannel iopub = null;
    public JupyterChannel stdin = null;
    public JupyterChannel hb = null;
    public JupyterChannel control = null;

    private Thread shellThread = null;
    private Thread iopubThread = null;
    private Thread stdinThread = null;
    private Thread hbThread = null;
    private Thread controlThread = null;

    public Kernel () {
        // Instantiate objects that will be useful later
        this.parser = new JSONParser();

        // Retrieve the absolute path to resources/connexion_files
        String tempPath = Kernel.class.getResource("connexion_files/example.json").getPath();
        pathToConnexionFiles = tempPath.replace("/example.json", "");

        try {
            // Start a new container from image antoinecheronirisa/lmt-python-core
            startContainer();

            // Retrieve the file containing the connection_info from the kernel. These information are used to connect
            // to the proper zmq sockets
            if(this.containerId != null) {
                // Path to the connection_info file
                String absolutePathToConnexionInfoFile = pathToConnexionFiles + "/" + containerId + ".json";

                // We wait until the file has been created
                File f = new File(absolutePathToConnexionInfoFile);
                int timeout = 10000;
                int elapse = 0;
                while (!f.exists() || elapse >= timeout) {
                    Thread.sleep(100);
                    elapse += 100;
                }

                // Parse the file to retrieve the interesting informations
                Object file = parser.parse(new FileReader(absolutePathToConnexionInfoFile));
                JSONObject connexionInfo = (JSONObject) file;

                // Read and save all the network and messaging information from the file
                this.transport = (String) connexionInfo.get("transport");
                this.ip = retrieveContainerIp();
                this.signature_scheme = (String) connexionInfo.get("signature_scheme");
                this.key = (String) connexionInfo.get("key");

                // Retrieve sockets port from the file
                long shell_port = (Long) connexionInfo.get("shell_port");
                long iopub_port = (Long) connexionInfo.get("iopub_port");
                long stdin_port = (Long) connexionInfo.get("stdin_port");
                long hb_port = (Long) connexionInfo.get("hb_port");
                long control_port = (Long) connexionInfo.get("control_port");

                // Initialize the channels
                this.shell = new ShellChannel("shell", transport, ip, shell_port, containerId);
                this.iopub = new IOPubChannel("iopub", transport, ip, iopub_port, containerId);
                this.stdin = new StdinChannel("stdin", transport, ip, stdin_port, containerId);
                this.hb = new HeartbeatChannel("hb", transport, ip, hb_port, containerId);
                this.control = new ShellChannel("control", transport, ip, control_port, containerId);

                // Create & start the threads
                shellThread = new Thread(shell);
                iopubThread = new Thread(iopub);
                stdinThread = new Thread(stdin);
                hbThread = new Thread(hb);
                controlThread = new Thread(control);
                startChannels();
            }

        } catch (FailedKernelStartException | FailedRetrievingContainerIPException | IOException | ParseException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void startContainer() throws FailedKernelStartException {

        File script = null;

        try {
            // Write the bash script that will start the container
            script = File.createTempFile("script", null);
            Writer streamWriter = new OutputStreamWriter(new FileOutputStream(script));
            PrintWriter printWriter = new PrintWriter(streamWriter);
            printWriter.println("#!/bin/bash");
            printWriter.println("docker run -d --rm -v " + pathToConnexionFiles + ":/home/diverse/connexion_files antoinecheronirisa/lmt-python-core");
            printWriter.println("exit");
            printWriter.close();

            // Create the object that let us start run the command
            ProcessBuilder pb = new ProcessBuilder("bash", script.toString());

            // Start container
            this.container = pb.start();

            // Retrieve the outpustream to read the container id

            BufferedReader in = new BufferedReader(new InputStreamReader(this.container.getInputStream()));

            // The outputstream is only one line long and contains the newly created container's id
            this.containerId = in.readLine().substring(0,12);
            System.out.println(containerId);
            String line = null;
            while((line=in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new FailedKernelStartException();
        } finally {
            // Everything went well
            if (script != null) script.delete();
        }

    }

    public void stopContainer () {
        // TODO make it working properly
        stopChannels();
        container.destroy();
    }

    public void startChannels () {
        shellThread.start();
        iopubThread.start();
        stdinThread.start();
        hbThread.start();
        controlThread.start();
    }

    public void stopChannels() {
        // TODO
    }

    private String retrieveContainerIp() throws FailedRetrievingContainerIPException {
        // Path to the script used to get a running container's ip address
        String pathToScript = "src/main/resources/retrieve-container-ip.sh";

        try {
            ProcessBuilder pb = new ProcessBuilder( pathToScript, containerId);

            // Runs the command to get its result (the ip address)
            Process proc = pb.start();

            // Retrieve the outputstream to read the result
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;

            // Go through the inputstream (that correspond to the standard output from the executed script)
            while((line = in.readLine()) != null) {
                if (isIpAddress(line)) return line;
            }

        } catch (Exception e) {
            throw new FailedRetrievingContainerIPException(this.containerId);
        }

        return null;
    }

    /* =================================================================================================================
       =================================================================================================================
                                                    SETTERS AND GETTERS FUNCTIONS
       =================================================================================================================
       ===============================================================================================================*/

    public String getContainerId () {
        return this.containerId;
    }

    /* =================================================================================================================
       =================================================================================================================
                                                    UTILITY FUNCTIONS
       =================================================================================================================
       ===============================================================================================================*/
    private boolean isIpAddress(String text) {
        // Define the REGEX for an ip address
        String IPADDRESS_PATTERN =
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

        // Create a pattern object used to run the regex
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);

        // Create an object that verify whether the given "text" match the regex previously given
        Matcher matcher = pattern.matcher(text);

        // Run and return the test
        return matcher.matches();
    }

    /* =================================================================================================================
       =================================================================================================================
                                                    EXCEPTION CLASSES
       =================================================================================================================
       ===============================================================================================================*/

    public class FailedKernelStartException extends Exception {
        public FailedKernelStartException() {
            super("[ERROR] Failed trying to start the docker container. Please verify that your docker daemon is running");
        }
    }

    public class FailedRetrievingContainerIPException extends Exception {
        public FailedRetrievingContainerIPException(String containerId) {
            super("Unable to retrieve ip of container with ID : "
                    + containerId + ". Verify that this container is running");
        }
    }
}
