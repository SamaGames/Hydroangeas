package net.samagames.hydroangeas.server.connection;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.packets.AbstractPacket;
import net.samagames.hydroangeas.common.packets.ConnectionManager;
import net.samagames.hydroangeas.common.protocol.coupaings.CoupaingServerPacket;
import net.samagames.hydroangeas.common.protocol.intranet.*;
import net.samagames.hydroangeas.common.protocol.queues.QueueAddPlayerPacket;
import net.samagames.hydroangeas.common.protocol.queues.QueueAttachPlayerPacket;
import net.samagames.hydroangeas.common.protocol.queues.QueueDetachPlayerPacket;
import net.samagames.hydroangeas.common.protocol.queues.QueueRemovePlayerPacket;
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
public class ServerConnectionManager extends ConnectionManager
{

    public HydroangeasServer instance;

    public ServerConnectionManager(Hydroangeas hydroangeas)
    {
        super(hydroangeas);
        instance = hydroangeas.getAsServer();
    }

    public void sendPacket(HydroClient client, AbstractPacket packet)
    {
        String channel = "global@" + client.getUUID() + "@hydroangeas-client";
        sendPacket(channel, packet);
    }

    public void sendPacket(UUID client, AbstractPacket packet)
    {
        String channel = "global@" + client + "@hydroangeas-client";
        sendPacket(channel, packet);
    }

    @Override
    public void handler(int id, String data)
    {
        Object spacket = gson.fromJson(data, packets[id].getClass());

        if (spacket instanceof HeartbeatPacket)
        {
            HeartbeatPacket heartbeatPacket = (HeartbeatPacket) spacket;
            instance.getClientManager().onClientHeartbeat(heartbeatPacket.getUUID());
        } else if (spacket instanceof MinecraftServerIssuePacket)
        {
            MinecraftServerIssuePacket packet = (MinecraftServerIssuePacket) spacket;
            hydroangeas.log(Level.SEVERE, "An error occurred with the client '" + packet.getUUID() + "'!");
            hydroangeas.log(Level.SEVERE, "> Category: Server issue (" + packet.getIssueType().name() + ")");

            HydroClient client = instance.getClientManager().getClientByUUID(packet.getUUID());
            if (client == null)
            {
                return;
            }

            MinecraftServerS server = client.getServerManager().getServerByName(packet.getServerName());
            if (server.isHub())
            {
                instance.getHubBalancer().onHubShutdown(server);
            }
            ModMessage.sendError(InstanceType.SERVER, packet.getMessage());
        } else if (spacket instanceof MinecraftServerInfoPacket)
        {
            MinecraftServerInfoPacket packet = (MinecraftServerInfoPacket) spacket;

            HydroClient client = instance.getClientManager().getClientByUUID(packet.getUUID());
            if (client == null)
            {
                return;
            }

            client.getServerManager().handleServerData(packet);

        } else if (spacket instanceof MinecraftServerUpdatePacket)
        {
            MinecraftServerUpdatePacket packet = (MinecraftServerUpdatePacket) spacket;

            HydroClient client = instance.getClientManager().getClientByUUID(packet.getUUID());
            if (client == null)
            {
                return;
            }

            MinecraftServerS server = client.getServerManager().getServerByName(packet.getServerName());

            //Handle pour l'algo avant de prendre les action
            try
            {
                instance.getAlgorithmicMachine().onServerUpdate(client, server, packet);
            } catch (Exception e)
            {
            }

            switch (packet.getAction())
            {
                case START:
                    server.setStarted(true);
                    //TODO add event ?
                    break;
                case END:
                    client.getServerManager().removeServer(packet.getServerName());
                    break;
            }

        } else if (spacket instanceof ByeFromClientPacket)
        {
            instance.getClientManager().onClientNoReachable(((ByeFromClientPacket) spacket).getUUID());
        } else if (spacket instanceof CoupaingServerPacket)
        {
            instance.getClientManager().orderServerForCoupaing((CoupaingServerPacket) spacket);
        } else if (spacket instanceof HelloFromClientPacket)
        {
            instance.getClientManager().updateClient((HelloFromClientPacket) spacket);
        } else if (spacket instanceof QueueAddPlayerPacket)
        {
            instance.getQueueManager().handlePacket((QueueAddPlayerPacket) spacket);
        } else if (spacket instanceof QueueRemovePlayerPacket)
        {
            instance.getQueueManager().handlePacket((QueueRemovePlayerPacket) spacket);
        } else if (spacket instanceof QueueAttachPlayerPacket)
        {
            instance.getQueueManager().handlePacket((QueueAttachPlayerPacket) spacket);
        } else if (spacket instanceof QueueDetachPlayerPacket)
        {
            instance.getQueueManager().handlePacket((QueueDetachPlayerPacket) spacket);
        }
    }
}
