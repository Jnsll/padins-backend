package fr.irisa.diverse.FBPNetworkProtocolUtils;

import java.util.Date;

/**
 * The status class stores information about the status of a specific Flow or Group (so one per network)
 *
 * Created by antoine on 02/06/17.
 */
public class Status {

    // Attributes
    private long startedTime;
    private long stoppedTime;
    private long uptime;
    private boolean debug;
    private Date date;


    /* =================================================================================================================
                                                    CONSTRUCTOR
       ===============================================================================================================*/
    public Status () {
        startedTime = 0;
        stoppedTime = 0;
        uptime = 0;
        debug = false;
        date = new Date();
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    /**
     * Give the time when the network first started
     *
     * @return The timestamp of the moment when the network first started
     */
    public long getStartedTime () {
        return startedTime;
    }

    /**
     * Give the time when the network stopped the last time
     *
     * @return The timestamp of the moment when the network stopped
     */
    public long getStoppedTime () {
        return stoppedTime;
    }

    /**
     * Tell the Status instance that the network started.
     */
    public void start () {
        this.startedTime = date.getTime();
    }

    /**
     * Tell the Status instance that the network stopped.
     */
    public void stop () {
        this.stoppedTime = date.getTime();
        uptime += stoppedTime - startedTime;
    }

    /**
     * Gives the total running duration of the network.
     *
     * @return The total running duration of the network.
     */
    public long getUptime () {
        if (stoppedTime == 0) uptime = date.getTime() - startedTime;
        return uptime;
    }

    /**
     * Tell whether the network ever started or not.
     *
     * @return true if the network has ever started.
     */
    public boolean hasStarted () {
        return startedTime != 0;
    }

    /**
     * Tell whether the network is currently running.
     *
     * @return true if running
     */
    public boolean isRunning () {
        return stoppedTime < startedTime;
    }

    /**
     * Tell whether the network is in debug mode or not
     *
     * @return if in debug mode
     */
    public boolean isInDebugMode () {
        return debug;
    }

    /**
     * Turn debug on only for status, don't do anything on the node or network
     */
    public void turnDebugOn () { this.debug = true; }

    /**
     * Turn debug off only for status, don't do anything on the node or network
     */
    public void turnDebugOff () { this.debug = false; }
}
