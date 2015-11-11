package net.samagames.hydroangeas.server.tasks;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.protocol.intranet.HeartbeatPacket;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.HydroClient;
import net.samagames.hydroangeas.server.client.MinecraftServerS;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by Silva on 09/10/2015.
 */
public class CleanServer {

    public final static long LIVETIME = 14400000L; //4 heures
    //private final static long LIVETIME = 60000L; //60 secondes for test
    private final HydroangeasServer instance;

    public CleanServer(HydroangeasServer instance)
    {
        this.instance = instance;
    }

    public void start()
    {
        instance.getScheduler().scheduleAtFixedRate(this::check, 300, 300, TimeUnit.SECONDS);
    }

    public void check()
    {
        for(HydroClient client : instance.getClientManager().getClients())
        {
            client.getServerManager().getServers().stream().filter(server -> System.currentTimeMillis() - server.getStartedTime() > server.getTimeToLive()).forEach(server -> {
                instance.getLogger().info("Scheduled shutdown for: " + server.getServerName());

                int timeToStop = 0;
                if (server.isHub()) {
                    server.dispatchCommand("evacuate lobby");
                    instance.getHubBalancer().onHubShutdown(server);
                    timeToStop = 65;
                } else {
                    server.dispatchCommand("stop");
                    timeToStop = 15;
                }

                instance.getScheduler().schedule(() -> server.shutdown(), timeToStop, TimeUnit.SECONDS);

            });
        }
    }

}
