package net.samagames.hydroangeas.server.receiver;

import com.google.gson.Gson;
import net.samagames.hydroangeas.common.packets.PacketReceiver;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.server.data.ServerStatus;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 13/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class ServerStatusReceiver implements PacketReceiver {

    public HydroangeasServer instance;

    public ServerStatusReceiver(HydroangeasServer instance)
    {
        this.instance = instance;
    }

    @Override
    public void receive(String packet) {
        ServerStatus data = new Gson().fromJson(packet, ServerStatus.class);

        String serverName = data.getBungeeName();

        MinecraftServerS server = instance.getClientManager().getServerByName(serverName);

        if(server == null)
        {
            instance.getLogger().info("Server: " + serverName + " not handled by Hydro");
            return;
        }
        server.setActualSlots(data.getPlayers());
        server.setStatus(data.getStatus());
    }
}
