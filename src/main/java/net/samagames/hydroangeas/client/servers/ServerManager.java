package net.samagames.hydroangeas.client.servers;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.packets.HelloClientPacket;
import net.samagames.hydroangeas.client.packets.MinecraftServerEndPacket;
import net.samagames.hydroangeas.client.schedulers.ServerCheckerThread;
import net.samagames.hydroangeas.common.protocol.MinecraftServerIssuePacket;
import net.samagames.hydroangeas.common.protocol.MinecraftServerOrderPacket;
import net.samagames.hydroangeas.common.protocol.MinecraftServerUpdatePacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ServerManager
{
    private final HydroangeasClient instance;
    private final List<MinecraftServer> servers = new ArrayList<>();
    private final HashMap<UUID, ScheduledFuture> scheduledFuture;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(12);

    public ServerManager(HydroangeasClient instance)
    {
        this.instance = instance;
        this.scheduledFuture = new HashMap<>();
    }

    public void newServer(MinecraftServerOrderPacket serverInfos)
    {
        int port = instance.getServerManager().getAvailablePort();
        MinecraftServer server = new MinecraftServer(this.instance, serverInfos, port);

        if(!server.makeServer())
        {
            instance.getConnectionManager().sendPacket(new MinecraftServerIssuePacket(this.instance.getClientUUID(), serverInfos.getServerName(), MinecraftServerIssuePacket.Type.MAKE));
            return;
        }

        if(!server.startServer())
        {
            instance.getConnectionManager().sendPacket(new MinecraftServerIssuePacket(this.instance.getClientUUID(), serverInfos.getServerName(), MinecraftServerIssuePacket.Type.START));
            return;
        }

        this.servers.add(server);
        this.scheduledFuture.put(serverInfos.getUUID(), this.scheduler.scheduleAtFixedRate(new ServerCheckerThread(this.instance, server), 10, 10, TimeUnit.SECONDS));

        this.instance.log(Level.INFO, "New server started -> Game (" + serverInfos.getGame() + ") & Map (" + serverInfos.getMap() + ")");

        instance.getConnectionManager().sendPacket(new MinecraftServerUpdatePacket(instance.getClientUUID(), server.getServerName(), MinecraftServerUpdatePacket.UType.START));
    }

    public void stopAll()
    {
        for(MinecraftServer server : this.servers)
        {
            if(!server.stopServer())
            {
                instance.getConnectionManager().sendPacket(
                        new MinecraftServerIssuePacket(this.instance.getClientUUID(), server.getServerName(), MinecraftServerIssuePacket.Type.STOP));
            }else
            {
                instance.getConnectionManager().sendPacket(new MinecraftServerUpdatePacket(instance.getClientUUID(), server.getServerName(), MinecraftServerUpdatePacket.UType.STOP));
            }
        }
    }

    public void onServerStop(MinecraftServer server)
    {
        this.scheduledFuture.get(server.getUUID()).cancel(true);
        this.scheduledFuture.remove(server.getUUID());
        this.servers.remove(server.getUUID());

        new HelloClientPacket(this.instance).send();
        new MinecraftServerEndPacket(server).send();
    }

    public int getAvailablePort()
    {
        return instance.findRandomOpenPort();
    }

    public MinecraftServer getServerByUUID(UUID uuid)
    {
        for(MinecraftServer server : servers)
        {
            if(server.getUUID().equals(uuid)
            {
                return server;
            }
        }
        return null;
    }

    public List<MinecraftServer> getServers()
    {
        return this.servers;
    }
}
