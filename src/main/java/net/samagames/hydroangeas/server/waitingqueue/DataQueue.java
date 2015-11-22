package net.samagames.hydroangeas.server.waitingqueue;

import net.samagames.hydroangeas.server.HydroangeasServer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Silva on 22/11/2015.
 */
public class DataQueue {

    private HydroangeasServer instance;

    //Number of started server in the last 10 minutes
    private AtomicInteger numberOfServerStarted = new AtomicInteger(0);

    private final static double NumberOfServerPerMinute = 1.0;

    public DataQueue(HydroangeasServer instance)
    {
        this.instance = instance;

    }

    public void startedServer()
    {
        //Increment now
        numberOfServerStarted.incrementAndGet();

        //Decrement in 10 minutes
        instance.getScheduler().schedule(() -> numberOfServerStarted.decrementAndGet(), 10L, TimeUnit.MINUTES);
    }

    public boolean needToAnticipate()
    {
        return ((double)numberOfServerStarted.get())/10.0 > NumberOfServerPerMinute;
    }
}
