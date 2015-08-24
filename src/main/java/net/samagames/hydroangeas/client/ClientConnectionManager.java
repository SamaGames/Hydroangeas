package net.samagames.hydroangeas.client;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;
import net.samagames.hydroangeas.common.packets.AbstractPacket;
import net.samagames.hydroangeas.common.packets.ConnectionManager;
import net.samagames.hydroangeas.common.protocol.intranet.AskForClientActionPacket;
import net.samagames.hydroangeas.common.protocol.intranet.AskForClientDataPacket;
import net.samagames.hydroangeas.common.protocol.intranet.HeartbeatPacket;
import net.samagames.hydroangeas.common.protocol.intranet.MinecraftServerOrderPacket;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 25/06/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class ClientConnectionManager extends ConnectionManager {

    public HydroangeasClient instance;

    public ClientConnectionManager(Hydroangeas hydroangeas) {
        super(hydroangeas);

        instance = hydroangeas.getAsClient();
    }

    public void sendPacket(AbstractPacket packet)
    {
        String channel = "global@hydroangeas-server";
        sendPacket(channel, packet);
    }

    @Override
    public void handler(int id, String data) {
        Object spacket = gson.fromJson(data, packets[id].getClass());

        if(spacket instanceof HeartbeatPacket)
        {
            HeartbeatPacket heartbeatPacket = (HeartbeatPacket) spacket;
            instance.getLifeThread().onServerHeartbeat(heartbeatPacket.getUUID());
        }else if(spacket instanceof MinecraftServerOrderPacket)
        {
            MinecraftServerOrderPacket packet = (MinecraftServerOrderPacket) spacket;

            Hydroangeas.getInstance().getAsClient().getServerManager().newServer(packet);
        }else if(spacket instanceof AskForClientDataPacket)
        {
            AskForClientDataPacket packet = (AskForClientDataPacket) spacket;
            Hydroangeas.getInstance().getAsClient().getLifeThread().sendData(true);
        }else if(spacket instanceof AskForClientActionPacket)
        {
            AskForClientActionPacket packet = (AskForClientActionPacket) spacket;

            switch (packet.getCommand())
            {
                case SERVEREND:
                    MinecraftServerC server = instance.getServerManager().getServerByName(packet.getData());
                    server.stopServer();
                    break;
                case CLIENTSHUTDOWN:
                    System.exit(0);
                    break;
                case CONSOLECOMMAND:
                    instance.getCommandManager().inputCommand(packet.getData());
                    break;
            }
        }
    }
}
