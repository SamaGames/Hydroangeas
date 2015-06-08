package net.samagames.hydroangeas.client.schedulers;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.packets.HelloClientPacket;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class LifeThread
{
    private final HydroangeasClient instance;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture scheduledFuture;
    private boolean beforeConnected;
    private boolean connected;

    public LifeThread(HydroangeasClient instance)
    {
        this.instance = instance;
        this.scheduler = Executors.newScheduledThreadPool(4);
        this.beforeConnected = true;
        this.connected = false;
    }

    public void start()
    {
        this.scheduledFuture = this.scheduler.scheduleAtFixedRate(() ->
        {
            new HelloClientPacket(this.instance).send();

            try
            {
                Thread.sleep(5000);
            }
            catch (InterruptedException ignored) {}

            if(!this.connected)
            {
                ModMessage.sendMessage(InstanceType.CLIENT, "[" + this.instance.getClientName() + "] Impossible de contacter le serveur Hydroangeas !");

                this.instance.log(Level.SEVERE, "Can't tell the Hydroangeas Server! Maybe it's down?");
                this.beforeConnected = false;
            }
            else if(this.connected && !this.beforeConnected)
            {
                ModMessage.sendMessage(InstanceType.CLIENT, "[" + this.instance.getClientName() + "] Retour à la normale !");

                this.instance.log(Level.INFO, "Hydroangeas Server has responded! Back to normal!");
                this.beforeConnected = true;
            }

            this.connected = false;
        }, 0, 60, TimeUnit.SECONDS);
    }

    public void stop()
    {
        this.scheduledFuture.cancel(true);
    }

    public void connectedToServer()
    {
        this.connected = true;
    }
}
