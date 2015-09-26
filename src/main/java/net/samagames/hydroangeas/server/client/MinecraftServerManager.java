package net.samagames.hydroangeas.server.client;

import com.google.gson.JsonElement;
import net.samagames.hydroangeas.common.protocol.intranet.AskForClientActionPacket;
import net.samagames.hydroangeas.common.protocol.intranet.MinecraftServerInfoPacket;
import net.samagames.hydroangeas.common.protocol.intranet.MinecraftServerOrderPacket;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.games.BasicGameTemplate;
import net.samagames.hydroangeas.utils.MiscUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 25/06/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class MinecraftServerManager {

    private HydroangeasServer instance;

    private HydroClient client;
    private List<MinecraftServerS> servers = new ArrayList<>();

    public MinecraftServerManager(HydroangeasServer instance, HydroClient client)
    {
        this.instance = instance;
        this.client = client;
    }

    public MinecraftServerS addServer(BasicGameTemplate template)
    {
        MinecraftServerS server = new MinecraftServerS(client, template);

        // Make sure that the server name is not already taken
        while(instance.getClientManager().getServerByName(server.getServerName()) != null)
        {
            server.changeUUID();
        }

        server.setWeight(MiscUtils.calculServerWeight(server.getGame(), server.getMaxSlot(), server.isCoupaingServer()));

        servers.add(server);

        instance.getConnectionManager().sendPacket(client, new MinecraftServerOrderPacket(server));

        return server;
    }

    public void handleServerData(MinecraftServerInfoPacket packet)
    {
        MinecraftServerS server = getServerByName(packet.getServerName());
        //Server not in here so add it
        if(server == null)
        {
            instance.getLogger().severe("Error sync! server: " + packet.getServerName() + " not know by Hydroserver!");

            server = new MinecraftServerS(client, packet);

            if(getServerByUUID(server.getUUID()) != null)
            {
                instance.getLogger().severe("Error duplicated UUID ! Not saving server !");
                instance.getConnectionManager().sendPacket(client,
                        new AskForClientActionPacket(instance.getUUID(), AskForClientActionPacket.ActionCommand.SERVEREND, packet.getServerName()));
                return;
            }
            server.setWeight(MiscUtils.calculServerWeight(server.getGame(), server.getMaxSlot(), server.isCoupaingServer()));
            servers.add(server);
            instance.getLogger().info("Added " + packet.getServerName());
        }else{//Server here ! so update it !

            //First check correspondance between uuid and serverName
            if(!server.getUUID().equals(packet.getServerUUID()))
            {
                instance.getLogger().severe("Error server: " + server.getServerName() + " has not the same UUID");
                instance.getConnectionManager().sendPacket(client,
                        new AskForClientActionPacket(instance.getUUID(), AskForClientActionPacket.ActionCommand.SERVEREND, packet.getServerName()));
                return;
            }
            server.setPort(packet.getPort());
        }
    }

    public void removeServer(String serverName)
    {
        MinecraftServerS server = getServerByName(serverName);
        if(server == null)
            return;

        server.onShutdown();
        servers.remove(server);
    }

    public MinecraftServerS getServerByName(String serverName)
    {
        for(MinecraftServerS server : servers)
            if(server.getServerName().equals(serverName))
                return server;
        return null;
    }

    public MinecraftServerS getServerByUUID(UUID uuid)
    {
        for(MinecraftServerS server : servers)
        {
            if(server.getUUID().equals(uuid))
            {
                return server;
            }
        }
        return null;
    }

    public List<MinecraftServerS> getServersByTemplate(BasicGameTemplate template)
    {
        return servers.stream().filter(server -> server.getTemplateID().equals(template.getId())).collect(Collectors.toList());
    }

    public int getTotalWeight()
    {
        int weight = 0;
        for(MinecraftServerS serverS : servers)
        {
            weight += serverS.getWeight();
        }
        return weight;
    }

    public List<MinecraftServerS> getServers()
    {
        return servers;
    }


}
