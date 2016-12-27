package net.samagames.hydroangeas.client.servers;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.common.protocol.intranet.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class ServerManager
{
    private final HydroangeasClient instance;
    private final List<MinecraftServerC> servers = new ArrayList<>();

    public ServerManager(HydroangeasClient instance)
    {
        this.instance = instance;
    }

    public void newServer(MinecraftServerSyncPacket serverInfos)
    {
        try
        {
            //Check state of hydro
            checkTemplate(serverInfos.getTemplateID());

            int port = getAvailablePort();
            MinecraftServerC server = new MinecraftServerC(this.instance, serverInfos, port);

            instance.getLogger().info("Server creation !");

            if (!server.makeServer())
            {
                instance.getConnectionManager().sendPacket(new MinecraftServerIssuePacket(this.instance.getClientUUID(), serverInfos.getServerName(), MinecraftServerIssuePacket.Type.MAKE));
                instance.getConnectionManager().sendPacket(new MinecraftServerUpdatePacket(instance, server.getServerName(), MinecraftServerUpdatePacket.UType.END));
                server.stopServer();
                return;
            }
            if (!server.startServer())
            {
                instance.getConnectionManager().sendPacket(new MinecraftServerIssuePacket(this.instance.getClientUUID(), serverInfos.getServerName(), MinecraftServerIssuePacket.Type.START));
                instance.getConnectionManager().sendPacket(new MinecraftServerUpdatePacket(instance, server.getServerName(), MinecraftServerUpdatePacket.UType.END));
                server.stopServer();
                return;
            }
            this.servers.add(server);

            this.instance.log(Level.INFO, "New server started -> Game (" + serverInfos.getGame() + ") & Map (" + serverInfos.getMap() + ")");

            instance.getConnectionManager().sendPacket(new MinecraftServerUpdatePacket(instance, server.getServerName(), MinecraftServerUpdatePacket.UType.START));
            //Complete data of the server
            instance.getConnectionManager().sendPacket(new MinecraftServerSyncPacket(instance, server));
        } catch (Exception e)
        {
            e.printStackTrace();
            instance.getConnectionManager().sendPacket(new MinecraftServerIssuePacket(this.instance.getClientUUID(), serverInfos.getServerName(), MinecraftServerIssuePacket.Type.MAKE));
            instance.getConnectionManager().sendPacket(new MinecraftServerUpdatePacket(instance, serverInfos.getServerName(), MinecraftServerUpdatePacket.UType.END));
        }
    }

    public void stopAll()
    {
        for (MinecraftServerC server : servers)
            instance.getConnectionManager().sendPacket(!server.stopServer() ? new MinecraftServerIssuePacket(this.instance.getClientUUID(), server.getServerName(), MinecraftServerIssuePacket.Type.STOP) : new MinecraftServerUpdatePacket(instance, server.getServerName(), MinecraftServerUpdatePacket.UType.END));
    }

    public void onServerStop(MinecraftServerC server)
    {
        instance.getConnectionManager().sendPacket(new MinecraftServerUpdatePacket(instance, server.getServerName(), MinecraftServerUpdatePacket.UType.END));
        this.servers.remove(server);
        instance.getLogger().info("Stopped server " + server.getServerName());
    }

    //Only number dividable by 2 and not used by an other server
    private int getAvailablePort()
    {
        boolean isUsed;
        int i;

        do{
            isUsed = false;
            i = ThreadLocalRandom.current().nextInt(20000, 40000);
            if (i % 2 != 0)
            {
                i++;
            }

            for (MinecraftServerC c : getServers())
            {
                if (c.getPort() == i)
                {
                    isUsed = true;
                }
            }
        }
        while (isUsed);

        return i;
    }

    public int getWeightOfAllServers()
    {
        int w = 0;
        List<MinecraftServerC> servers = new ArrayList<>();
        servers.addAll(this.servers);
        for (MinecraftServerC server : servers)
        {
            w += server.getWeight();
        }
        return w;
    }

    public MinecraftServerC getServerByName(String name)
    {
        for (MinecraftServerC server : servers)
        {
            if (server.getServerName().equals(name))
            {
                return server;
            }
        }
        return null;
    }

    public MinecraftServerC getServerByUUID(UUID uuid)
    {
        for (MinecraftServerC server : servers)
        {
            if (server.getUUID().equals(uuid))
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

    public void checkTemplate(String template) throws Exception {
        if(instance.getRestrictionMode().equals(HydroangeasClient.RestrictionMode.NONE))
            return;

        if(instance.getRestrictionMode().equals(HydroangeasClient.RestrictionMode.WHITELIST))
        {
            if(!instance.getWhitelist().contains(template))
            {
                throw new Exception("Try to start a server with template not whitelisted !");
            }
        }

        if(instance.getRestrictionMode().equals(HydroangeasClient.RestrictionMode.BLACKLIST)) {
            if (instance.getBlacklist().contains(template)) {
                throw new Exception("Try to start a server with a template blacklisted !");
            }
        }
    }

}
