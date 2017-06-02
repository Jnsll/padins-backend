package fr.irisa.diverse.FBPNetworkProtocolUtils;

/**
 * Created by antoine on 02/06/17.
 */
public class Status {

    // Attributes
    private long startedTime;
    private long stoppedTime;
    private long uptime;
    private boolean started;
    private boolean running;
    private boolean debug;


    // Constructor
    public Status () {
        startedTime = 0;
        stoppedTime = 0;
        uptime = 0;
        started = false;
        running = false;
        debug = false;
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    public long getStartedTime () {
        return startedTime;
    }

    public long getStoppedTime () {
        return stoppedTime;
    }

    public void setStartedTime (long time) {
        this.startedTime = time;
    }

    public void setStoppedTime (long time) {
        this.stoppedTime = time;
    }

    public long getUptime () {
        return uptime;
    }

    public boolean hasStarted () {
        return started;
    }

    public boolean isRunning () {
        return running;
    }

    public boolean isInDebugMode () {
        return debug;
    }
}
