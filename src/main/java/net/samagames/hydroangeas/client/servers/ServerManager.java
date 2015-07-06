package net.samagames.hydroangeas.client.servers;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.common.protocol.MinecraftServerIssuePacket;
import net.samagames.hydroangeas.common.protocol.MinecraftServerOrderPacket;
import net.samagames.hydroangeas.common.protocol.MinecraftServerUpdatePacket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class ServerManager
{
    private final HydroangeasClient instance;
    private final List<MinecraftServerC> servers = new ArrayList<>();

    public ServerManager(HydroangeasClient instance)
    {
        this.instance = instance;
    }

    public void newServer(MinecraftServerOrderPacket serverInfos)
    {
        int port = getAvailablePort();
        MinecraftServerC server = new MinecraftServerC(this.instance, serverInfos, port);

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

        this.instance.log(Level.INFO, "New server started -> Game (" + serverInfos.getGame() + ") & Map (" + serverInfos.getMap() + ")");

        instance.getConnectionManager().sendPacket(new MinecraftServerUpdatePacket(instance, server.getServerName(), MinecraftServerUpdatePacket.UType.START));
    }

    public void stopAll()
    {
        for(MinecraftServerC server : this.servers)
        {
            if(!server.stopServer())
            {
                instance.getConnectionManager().sendPacket(
                        new MinecraftServerIssuePacket(this.instance.getClientUUID(), server.getServerName(), MinecraftServerIssuePacket.Type.STOP));
            }else
            {
                instance.getConnectionManager().sendPacket(new MinecraftServerUpdatePacket(instance, server.getServerName(), MinecraftServerUpdatePacket.UType.END));
            }
        }
    }

    public void onServerStop(MinecraftServerC server)
    {
        instance.getConnectionManager().sendPacket(new MinecraftServerUpdatePacket(instance, server.getServerName(), MinecraftServerUpdatePacket.UType.END));
        this.servers.remove(server);
    }

    public int getAvailablePort()
    {
        return instance.findRandomOpenPort();
    }

    public int getWeightOfAllServers()
    {
        int w = 0;
        for(MinecraftServerC server : servers)
        {
            w += server.getWeight();
        }
        return w;
    }

    public MinecraftServerC getServerByUUID(UUID uuid)
    {
        for(MinecraftServerC server : servers)
        {
            if(server.getUUID().equals(uuid))
            {
                return server;
            }
        }
        return null;
    }

    public List<MinecraftServerC> getServers()
    {
        return this.servers;
    }
}
