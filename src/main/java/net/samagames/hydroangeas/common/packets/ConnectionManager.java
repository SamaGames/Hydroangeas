package net.samagames.hydroangeas.common.packets;

import com.google.gson.Gson;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.protocol.CoupaingServerPacket;
import net.samagames.hydroangeas.common.protocol.HeartbeatPacket;
import net.samagames.hydroangeas.common.protocol.HelloFromClientPacket;

import java.util.logging.Level;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 25/06/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public abstract class ConnectionManager {

    public Class<? extends AbstractPacket>[] packets = new Class[256];

    public Gson gson;
    protected Hydroangeas hydroangeas;

    public ConnectionManager(Hydroangeas hydroangeas)
    {
        packets[0] = HeartbeatPacket.class;
        packets[1] = HelloFromClientPacket.class;
        packets[2] = CoupaingServerPacket.class;

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

        this.handler(Integer.valueOf(id), packet);
    }

    public abstract void handler(int id, String packet);
}
