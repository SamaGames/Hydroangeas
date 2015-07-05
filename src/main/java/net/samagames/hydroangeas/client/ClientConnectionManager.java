package net.samagames.hydroangeas.client;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.packets.AbstractPacket;
import net.samagames.hydroangeas.common.packets.ConnectionManager;
import net.samagames.hydroangeas.common.protocol.HeartbeatPacket;
import net.samagames.hydroangeas.common.protocol.MinecraftServerOrderPacket;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 25/06/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class ClientConnectionManager extends ConnectionManager {

    public ClientConnectionManager(Hydroangeas hydroangeas) {
        super(hydroangeas);
    }

    public void sendPacket(AbstractPacket packet)
    {
        String channel = "global@hydroangeas-server";
        sendPacket(channel, packet);
    }

    @Override
    public void handler(int id, String data) {
        Object spacket = gson.fromJson(data, packets[id]);

        if(spacket instanceof HeartbeatPacket)
        {
            HeartbeatPacket heartbeatPacket = (HeartbeatPacket) spacket;
            hydroangeas.getAsClient().getLifeThread().onServerHeartbeat(heartbeatPacket.getUUID());
        }else if(spacket instanceof MinecraftServerOrderPacket)
        {
            MinecraftServerOrderPacket packet = (MinecraftServerOrderPacket) spacket;

            Hydroangeas.getInstance().getAsClient().getServerManager().newServer(packet);
        }
    }
}
