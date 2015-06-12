package net.samagames.hydroangeas.server.scheduler;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StartThread
{
    private final ScheduledExecutorService scheduler;

    public StartThread()
    {
        this.scheduler = Executors.newScheduledThreadPool(4);
    }

    public void start()
    {
        ModMessage.sendMessage(InstanceType.SERVER, "Démarrage d'Hydroangeas Server...");
        ModMessage.sendMessage(InstanceType.SERVER, "> Assimilation des données éxistantes (60 secondes)...");

        this.scheduler.schedule(() -> Hydroangeas.getInstance().getAsServer().getAlgorithmicMachine().startMachinery(), 60, TimeUnit.SECONDS);
    }
}
