package net.samagames.hydroangeas.client.servers;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.packets.MinecraftServerIssuePacket;
import net.samagames.hydroangeas.common.informations.MinecraftServerInfos;

import java.util.HashMap;
import java.util.UUID;

public class ServerManager
{
    private final HydroangeasClient instance;
    private final HashMap<UUID, MinecraftServer> servers;

    public ServerManager(HydroangeasClient instance)
    {
        this.instance = instance;
        this.servers = new HashMap<>();
    }

    public void newServer(MinecraftServerInfos serverInfos)
    {
        MinecraftServer server = new MinecraftServer(this.instance, serverInfos);

        if(!server.makeServer())
        {
            new MinecraftServerIssuePacket(this.instance, serverInfos, MinecraftServerIssuePacket.Type.MAKE).send();
            return;
        }

        if(!server.startServer())
        {
            new MinecraftServerIssuePacket(this.instance, serverInfos, MinecraftServerIssuePacket.Type.START).send();
            return;
        }

        this.servers.put(serverInfos.getUUID(), server);
    }

    public void stopAll()
    {
        for(MinecraftServer server : this.servers.values())
        {
            if(!server.stopServer())
            {
                new MinecraftServerIssuePacket(this.instance, server.getServerInfos(), MinecraftServerIssuePacket.Type.STOP).send();
                return;
            }
        }
    }

    public MinecraftServer getServerByUUID(UUID uuid)
    {
        if(this.servers.containsKey(uuid))
            return this.servers.get(uuid);
        else
            return null;
    }

    public HashMap<UUID, MinecraftServer> getServers()
    {
        return this.servers;
    }
}
