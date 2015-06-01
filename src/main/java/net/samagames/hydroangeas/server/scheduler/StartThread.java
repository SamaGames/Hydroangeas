package net.samagames.hydroangeas.server.scheduler;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.informations.MinecraftServerInfos;
import net.samagames.hydroangeas.server.packets.MinecraftServerPacket;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;
import net.samagames.hydroangeas.utils.StatsUtils;

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
        ModMessage.sendMessage(InstanceType.SERVER, "> Etape 1 : Assimilation des données éxistantes (10 secondes)...");

        this.scheduler.schedule(() ->
        {
            MinecraftServerInfos infos = new MinecraftServerInfos("quake", "babylon");
            new MinecraftServerPacket(Hydroangeas.getInstance().getAsServer().getClientManager().getClients().values().iterator().next(), infos).send();
            StatsUtils.newServer(infos);

        }, 10, TimeUnit.SECONDS);
    }
}
