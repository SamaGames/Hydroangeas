package net.samagames.hydroangeas.server.connection;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.packets.AbstractPacket;
import net.samagames.hydroangeas.common.packets.ConnectionManager;
import net.samagames.hydroangeas.common.protocol.*;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.HydroClient;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

import java.util.UUID;
import java.util.logging.Level;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 25/06/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class ServerConnectionManager extends ConnectionManager{

    public HydroangeasServer instance;

    public ServerConnectionManager(Hydroangeas hydroangeas) {
        super(hydroangeas);
        instance = hydroangeas.getAsServer();
    }

    public void sendPacket(HydroClient client, AbstractPacket packet)
    {
        String channel = "global@" + client.getUUID().toString() + "@hydroangeas-client";
        sendPacket(channel, packet);
    }

    public void sendPacket(UUID client, AbstractPacket packet)
    {
        String channel = "global@" + client.toString() + "@hydroangeas-client";
        sendPacket(channel, packet);
    }

    @Override
    public void handler(int id, String data) {
        Object spacket = gson.fromJson(data, packets[id].getClass());

        if(spacket instanceof HeartbeatPacket)
        {
            HeartbeatPacket heartbeatPacket = (HeartbeatPacket) spacket;
            instance.getClientManager().onClientHeartbeat(heartbeatPacket.getUUID());
        }else if(spacket instanceof MinecraftServerIssuePacket)
        {
            MinecraftServerIssuePacket packet = (MinecraftServerIssuePacket) spacket;
            hydroangeas.log(Level.SEVERE, "An error occurred with the client '" + packet.getUUID().toString() + "'!");
            hydroangeas.log(Level.SEVERE, "> Category: Server issue (" + packet.getIssueType().name() + ")");

            ModMessage.sendError(InstanceType.SERVER, packet.getMessage());
        }else if(spacket instanceof MinecraftServerInfoPacket)
        {
            MinecraftServerInfoPacket packet = (MinecraftServerInfoPacket) spacket;

            HydroClient client = instance.getClientManager().getClientByUUID(packet.getUUID());
            if(client == null)
            {
                return;
            }

            client.getServerManager().handleServerData(packet);

        }else if(spacket instanceof MinecraftServerUpdatePacket)
        {
            MinecraftServerUpdatePacket packet = (MinecraftServerUpdatePacket) spacket;

            HydroClient client = instance.getClientManager().getClientByUUID(packet.getUUID());
            if(client == null)
            {
                return;
            }

            //Handle pour l'algo avant de prendre les action
            try{
                instance.getAlgorithmicMachine().onServerUpdate(packet);
            }catch(Exception e)
            {
                e.printStackTrace();
            }

            MinecraftServerS server = client.getServerManager().getServerByName(packet.getServerName());

            switch(packet.getAction())
            {
                case START:
                    server.setStarted(true);
                    //TODO add event ?
                    break;
                case END:
                    client.getServerManager().removeServer(packet.getServerName());
                    break;
            }

        }else if(spacket instanceof ByeFromClientPacket)
        {
            ByeFromClientPacket packet = (ByeFromClientPacket) spacket;
            instance.getClientManager().onClientNoReachable(packet.getUUID());

        }else if(spacket instanceof CoupaingServerPacket)
        {
            CoupaingServerPacket packet = (CoupaingServerPacket) spacket;
            instance.getClientManager().orderServerForCoupaing(packet);

        }else if(spacket instanceof HelloFromClientPacket)
        {
            HelloFromClientPacket packet = (HelloFromClientPacket) spacket;
            instance.getClientManager().updateClient(packet);
        }
    }
}
