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

    private final static long LIVETIME = 14400000L; //4 heures
    private final HydroangeasServer instance;

    public CleanServer(HydroangeasServer instance)
    {
        this.instance = instance;
    }

    public void start()
    {
        instance.getScheduler().scheduleAtFixedRate(this::check, 10, 300, TimeUnit.SECONDS);
    }

    public void check()
    {
        for(HydroClient client : instance.getClientManager().getClients())
        {
            client.getServerManager().getServers().stream().filter(server -> System.currentTimeMillis() - server.getStartedTime() > LIVETIME).forEach(server -> {
                if (server.isHub()) {
                    server.dispatchCommand("evacuate lobby");
                } else {
                    server.shutdown();
                }
            });
        }
    }

}
