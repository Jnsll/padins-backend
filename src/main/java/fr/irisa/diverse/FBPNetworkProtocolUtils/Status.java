package fr.irisa.diverse.FBPNetworkProtocolUtils;

import java.util.Date;

/**
 * Created by antoine on 02/06/17.
 */
public class Status {

    // Attributes
    private long startedTime;
    private long stoppedTime;
    private long uptime;
    private boolean debug;
    private Date date;


    // Constructor
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

    public long getStartedTime () {
        return startedTime;
    }

    public long getStoppedTime () {
        return stoppedTime;
    }

    public void start () {
        this.startedTime = date.getTime();
    }

    public void stop () {
        this.stoppedTime = date.getTime();
        uptime += stoppedTime - startedTime;
    }

    public long getUptime () {
        if (stoppedTime == 0) uptime = date.getTime() - startedTime;
        return uptime;
    }

    public boolean hasStarted () {
        return startedTime != 0;
    }

    public boolean isRunning () {
        return stoppedTime < startedTime;
    }

    public boolean isInDebugMode () {
        return debug;
    }

    public void turnDebugOn () { this.debug = true; }

    public void turnDebugOff () { this.debug = false; }
}
