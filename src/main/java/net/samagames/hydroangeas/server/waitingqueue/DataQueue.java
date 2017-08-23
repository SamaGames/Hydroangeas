package net.samagames.hydroangeas.server.waitingqueue;

import net.samagames.hydroangeas.server.HydroangeasServer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * This file is part of Hydroangeas.
 *
 * Hydroangeas is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hydroangeas is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hydroangeas.  If not, see <http://www.gnu.org/licenses/>.
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
