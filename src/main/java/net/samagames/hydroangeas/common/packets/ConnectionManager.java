package net.samagames.hydroangeas.common.packets;

import com.google.gson.Gson;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.protocol.coupaings.CoupaingServerPacket;
import net.samagames.hydroangeas.common.protocol.hubinfo.GameInfosToHubPacket;
import net.samagames.hydroangeas.common.protocol.intranet.*;
import net.samagames.hydroangeas.common.protocol.queues.*;

import java.util.logging.Level;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 25/06/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public abstract class ConnectionManager {

    public AbstractPacket[] packets = new AbstractPacket[256];

    protected Gson gson;
    protected Hydroangeas hydroangeas;

    protected ConnectionManager(Hydroangeas hydroangeas)
    {
        //Intranet
        packets[0] = new HeartbeatPacket();
        packets[1] = new HelloFromClientPacket();
        packets[2] = new CoupaingServerPacket();
        packets[3] = new AskForClientDataPacket();
        packets[4] = new AskForClientActionPacket();
        packets[5] = new ByeFromClientPacket();
        packets[6] = new MinecraftServerIssuePacket();
        packets[7] = new MinecraftServerOrderPacket();
        packets[8] = new MinecraftServerUpdatePacket();
        packets[9] = new MinecraftServerInfoPacket();


        //Queues Packets
        packets[100] = new QueueAddPlayerPacket();
        packets[101] = new QueueRemovePlayerPacket();
        packets[102] = new QueueAttachPlayerPacket();
        packets[103] = new QueueDetachPlayerPacket();
        packets[104] = new QueueInfosUpdatePacket();

        //HubInfos
        packets[110] = new GameInfosToHubPacket();

        this.hydroangeas = hydroangeas;

        gson = new Gson();
    }

    public void getPacket(String packet)
    {
        String id;
        try{
            id = packet.split(":")[0];
            if(id == null || packets[Integer.valueOf(id)] == null)
            {
                hydroangeas.log(Level.SEVERE, "Error bad packet ID in the channel");
                return;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            hydroangeas.log(Level.SEVERE, "Error packet no ID in the channel");
            return;
        }

        packet = packet.substring(id.length()+1, packet.length());

        final String finalPacket = packet;
        final String finalID = id;
        final ConnectionManager manager = this;
        new Thread(() -> {
            try {
                manager.handler(Integer.valueOf(finalID), finalPacket);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }).start();
    }

    protected int packetId(AbstractPacket p)
    {
        for (int i = 0; i < packets.length; i++)
        {
            if(packets[i] == null)
                continue;
            if(packets[i].getClass().equals(p.getClass()))
                return i;
        }
        return -1;
    }

    public void sendPacket(String channel, AbstractPacket data)
    {
        int i = packetId(data);
        if(i < 0)
        {
            hydroangeas.log(Level.SEVERE, "Bad packet ID: " + i);
            return;
        }else if(channel == null)
        {
            hydroangeas.log(Level.SEVERE, "Channel null !");
            return;
        }
        try{
            hydroangeas.getRedisSubscriber().send(channel, i + ":" +gson.toJson(data));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public abstract void handler(int id, String packet);
}
