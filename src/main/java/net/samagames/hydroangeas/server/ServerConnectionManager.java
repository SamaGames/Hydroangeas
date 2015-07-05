package net.samagames.hydroangeas.server;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.packets.AbstractPacket;
import net.samagames.hydroangeas.common.packets.ConnectionManager;
import net.samagames.hydroangeas.common.protocol.HeartbeatPacket;
import net.samagames.hydroangeas.common.protocol.MinecraftServerIssuePacket;
import net.samagames.hydroangeas.server.client.HydroClient;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

import java.util.logging.Level;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 25/06/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class ServerConnectionManager extends ConnectionManager{

    public ServerConnectionManager(Hydroangeas hydroangeas) {
        super(hydroangeas);
    }

    public void sendPacket(HydroClient client, AbstractPacket packet)
    {
        String channel = "global@" + client.getUUID().toString() + "@hydroangeas-client";
        sendPacket(channel, packet);
    }


    @Override
    public void handler(int id, String data) {
        Object spacket = gson.fromJson(data, packets[id]);

        if(spacket instanceof HeartbeatPacket)
        {
            HeartbeatPacket heartbeatPacket = (HeartbeatPacket) spacket;
            hydroangeas.getAsServer().getClientManager().onClientHeartbeat(heartbeatPacket.getUUID());
        }else if(spacket instanceof MinecraftServerIssuePacket)
        {
            MinecraftServerIssuePacket packet = (MinecraftServerIssuePacket) spacket;
            hydroangeas.log(Level.SEVERE, "An error occurred with the client '" + packet.getUUID().toString() + "'!");
            hydroangeas.log(Level.SEVERE, "> Category: Server issue (" + packet.getIssueType().name() + ")");

            ModMessage.sendError(InstanceType.SERVER, packet.getMessage());
        }
    }
}
