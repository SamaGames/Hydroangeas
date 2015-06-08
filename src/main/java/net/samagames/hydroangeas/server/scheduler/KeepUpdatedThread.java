package net.samagames.hydroangeas.server.scheduler;

import net.samagames.hydroangeas.server.HydroangeasServer;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class KeepUpdatedThread
{
    private final HydroangeasServer instance;
    private final HashMap<String, ScheduledFuture> clientsScheduler;

    private ScheduledExecutorService scheduler;
    private boolean doLoop;

    public KeepUpdatedThread(HydroangeasServer instance)
    {
        this.instance = instance;
        this.clientsScheduler = new HashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(60);
        this.doLoop = true;
    }

    public void start()
    {
        Thread thread = new Thread(() ->
        {
            while(this.doLoop)
            {
                for (String clientName : this.instance.getClientManager().getClients().keySet())
                    if (!this.clientsScheduler.containsKey(clientName))
                        this.clientsScheduler.put(clientName, this.scheduler.scheduleAtFixedRate(new ClientScheduledRunnable(this.instance, this.instance.getClientManager().getClientInfosByUUID(clientName)), 65, 65, TimeUnit.SECONDS));

                try
                {
                    Thread.sleep(10000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void stopClient(String clientName)
    {
        if(this.clientsScheduler.containsKey(clientName))
        {
            this.clientsScheduler.get(clientName).cancel(true);
            this.clientsScheduler.remove(clientName);
        }
    }

    public void stop()
    {
        this.doLoop = false;
        this.clientsScheduler.keySet().forEach(this::stopClient);
    }
}
