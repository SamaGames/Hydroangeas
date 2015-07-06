package net.samagames.hydroangeas.server.client;

import net.samagames.hydroangeas.common.protocol.MinecraftServerOrderPacket;
import net.samagames.hydroangeas.server.HydroangeasServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public void addServer(String game, String map, int minSlot, int maxSlot, HashMap<String, String> options)
    {
        MinecraftServerS server = new MinecraftServerS(game, map, minSlot, maxSlot, options);

        //Comme on prend que la première partie de l'uuid on check si un serveur a déja un nom identique
        while(instance.getClientManager().getServerByName(server.getServerName()) != null)
        {
            server.changeUUID();
        }

        servers.add(server);

        instance.getConnectionManager().sendPacket(client, new MinecraftServerOrderPacket(server));
    }

    public void removeServer(String serverName)
    {
        MinecraftServerS server = getServerByName(serverName);
        if(server == null)
        {
            return;
        }

        server.shutdown();
        servers.remove(server);
    }

    public MinecraftServerS getServerByName(String serverName)
    {
        for(MinecraftServerS server : servers)
        {
            if(server.getServerName().equals(serverName))
            {
                return server;
            }
        }
        return null;
    }

    public List<MinecraftServerS> getServers()
    {
        return servers;
    }


}
