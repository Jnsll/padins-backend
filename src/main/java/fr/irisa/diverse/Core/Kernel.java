package fr.irisa.diverse.Core;

import fr.irisa.diverse.JupyterChannels.*;
import fr.irisa.diverse.JupyterMessaging.Manager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The kernel is the element that runs the python code and informs every interface of the executed code, the result,
 * that it is busy, idle, etc.
 *
 * 5 channels are used to communicate with the fr.irisa.diverse.Core.Kernel (see fr.irisa.diverse.JupyterMessaging in Jupyter doc to know more about it).
 *
 * The kernel is embedded into a Docker container, which creates a connexion_file that this class will read
 * to know on which ports to connect the sockets.
 *
 * Created by antoine on 28/04/17.
 */
public class Kernel {

    // Process and Docker container id
    private Process container;
    private String containerId = null;

    // Channels
    public ShellChannel shell = null;
    public IOPubChannel iopub = null;
    public StdinChannel stdin = null;
    public HeartbeatChannel hb = null;
    public ShellChannel control = null;

    // Kernel state & execution info
    private boolean idle = false;
    private Long nbExecutions = Long.valueOf(0);

    // Messages info
    private String session = null;
    private String identity = null; // uuid of the messages exchanged on the channels
    private Manager messagesManager = null;

    // Channels' connexion infos
    private String transport = null;
    private String ip = null;
    private String signature_scheme = null;
    private String key = null;
    private String pathToConnexionFiles = null;
    private JSONParser parser = null;


    // Constructor
    public Kernel () {
        // Instantiate objects that will be useful later
        this.parser = new JSONParser();

        // Retrieve the absolute path to resources/connexion_files
        String tempPath = Kernel.class.getClassLoader().getResource("connexion_files/example.json").getPath();
        pathToConnexionFiles = tempPath.replace("/example.json", "");

        try {
            // Start a new container from image antoinecheronirisa/lmt-python-core
            startContainer();

            // Retrieve the file containing the connection_info from the kernel. These information are used to connect
            // to the proper zmq sockets
            if(this.containerId != null) {
                // Path to the connection_info file
                String absolutePathToConnexionInfoFile = pathToConnexionFiles + "/" + containerId + ".json";

                createChannelsFromConnexionFile(absolutePathToConnexionInfoFile);

                startChannels();

                // Create a message manager that will handle reaction to incoming messages
                messagesManager = new Manager(this);
            }

        } catch (FailedKernelStartException | FailedRetrievingContainerIPException | IOException | ParseException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void stop () {
        stopChannels();
        stopContainer();
        deleteConnexionFile();
    }

    public void verifyChannelsAreOk () {
        verifyChannelIsOk(shell);
        verifyChannelIsOk(iopub);
        verifyChannelIsOk(hb);
        verifyChannelIsOk(stdin);
        verifyChannelIsOk(control);
    }

    private void verifyChannelIsOk(JupyterChannel channel) {
        if (!channel.isRunning()) {
            try {
                channel.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            channel.start();
        }
    }

    /* =================================================================================================================
       =================================================================================================================
                                                    PRIVATE FUNCTIONS
       =================================================================================================================
       ===============================================================================================================*/

    private void startContainer() throws FailedKernelStartException {

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

    private void stopContainer () {
        // Run a script to stop the running container
        File script;

        try {
            // Write the bash script that will stop the running container
            script = File.createTempFile("script-stop", null);
            Writer streamWriter = new OutputStreamWriter(new FileOutputStream(script));
            PrintWriter printWriter = new PrintWriter(streamWriter);
            printWriter.println("#!/bin/bash");
            printWriter.println("docker stop " + containerId);
            printWriter.println("exit");
            printWriter.close();

            // Create the object that let us run the command
            ProcessBuilder pb = new ProcessBuilder("bash", script.toString());

            System.out.println("Stopping container " + containerId + "...");
            // Run the command that stop the container
            this.container = pb.start();

            // When stopping a container, docker output the container id after stopping it.
            // To make sure that the container stop before the program exit, we read the output stream in order
            // to make this program wait until the container stops.
            BufferedReader in = new BufferedReader(new InputStreamReader(this.container.getInputStream()));
            in.readLine();
            System.out.println("\033[32m" + "[INFO]" + "\033[0m" + " Container " + containerId + " successfully stopped");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Everything went well
            container.destroy();
        }
    }

    private void startChannels () {
        shell.start();
        iopub.start();
        stdin.start();
        hb.start();
        control.start();
    }

    private void stopChannels() {
        // Fix a bug that prevent from stopping
        shell.doLog(false);
        iopub.doLog(false);
        stdin.doLog(false);
        hb.doLog(false);
        control.doLog(false);

        try {
            shell.stop();
            iopub.stop();
            stdin.stop();
            hb.stop();
            control.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void createChannelsFromConnexionFile (String path) throws InterruptedException, FailedRetrievingContainerIPException, ParseException, IOException {
        // We wait until the file has been created
        File f = new File(path);
        int timeout = 10000;
        int elapse = 0;
        while (!f.exists() || elapse >= timeout) {
            Thread.sleep(100);
            elapse += 100;
        }

        // Parse the file to retrieve the interesting informations
        Object file = parser.parse(new FileReader(path));
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

        // Create the channels
        this.shell = new ShellChannel("shell", transport, ip, shell_port, containerId, this);
        this.iopub = new IOPubChannel("iopub", transport, ip, iopub_port, containerId, this);
        this.stdin = new StdinChannel("stdin", transport, ip, stdin_port, containerId, this);
        this.hb = new HeartbeatChannel("hb", transport, ip, hb_port, containerId, this);
        this.control = new ShellChannel("control", transport, ip, control_port, containerId, this);
    }

    /* =================================================================================================================
       =================================================================================================================
                                                    SETTERS AND GETTERS FUNCTIONS
       =================================================================================================================
       ===============================================================================================================*/

    public String getContainerId () {
        return this.containerId;
    }

    public String getSession () { return this.session != null ? this.session : ""; }

    public void setSession(String session) { this.session = session; }

    public String getIdentity () { return this.identity != null ? this.identity : ""; }

    public void setIdentity (String identity) {
        this.identity = identity;
        shell.setIdentity(identity);
        iopub.setIdentity(identity);
        hb.setIdentity(identity);
        control.setIdentity(identity);
        stdin.setIdentity(identity);
    }

    public String getSignatureScheme () { return signature_scheme != null ? signature_scheme : ""; }

    public String getKey () { return this.key != null ? this.key : ""; }

    public void setNbExecutions (Long nbExecutions) { this.nbExecutions = nbExecutions; }

    public Long getNbExecutions () { return nbExecutions; }

    public boolean isIdle () { return idle; }

    public boolean isBusy () { return !idle; }

    public void setIdleState (boolean value) {
        idle = value ;
    }

    public Manager getMessagesManager () { return messagesManager != null ? messagesManager : new Manager(this); }

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

    private String retrieveContainerIp() throws FailedRetrievingContainerIPException {
        // Path to the script used to get a running container's ip address
        String pathToScript = "src/main/resources/retrieve-container-ip.sh";

        try {
            ProcessBuilder pb = new ProcessBuilder( pathToScript, containerId);

            // Runs the command to get its result (the ip address)
            Process proc = pb.start();

            // Retrieve the outputstream to read the result
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;

            // Go through the input stream (that correspond to the standard output from the executed script)
            while((line = in.readLine()) != null) {
                if (isIpAddress(line)) return line;
            }

        } catch (Exception e) {
            throw new FailedRetrievingContainerIPException(this.containerId);
        }

        return "127.0.0.1";
    }

    private void deleteConnexionFile () {
        String absolutePathToConnexionInfoFile = pathToConnexionFiles + "/" + containerId + ".json";

        // We wait until the file has been created
        File f = new File(absolutePathToConnexionInfoFile);
        if (f.exists()) f.delete();
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
