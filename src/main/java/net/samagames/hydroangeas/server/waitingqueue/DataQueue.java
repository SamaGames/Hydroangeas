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

    private AtomicInteger lastServerStartNB = new AtomicInteger(0);

    private final static double NumberOfMinutePerServer = 10.0;

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
        return ((((double)numberOfServerStarted.get()) > 0) ? 10.0/((double)numberOfServerStarted.get()): Integer.MAX_VALUE) < NumberOfMinutePerServer;
    }

    public int getLastServerStartNB() {
        return lastServerStartNB.get();
    }

    public void setLastServerStartNB(int number) {
        this.lastServerStartNB.lazySet(number);
    }
}
