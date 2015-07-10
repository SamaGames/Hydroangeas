package net.samagames.hydroangeas.server.client;

import net.samagames.hydroangeas.common.protocol.HelloFromClientPacket;
import net.samagames.hydroangeas.server.HydroangeasServer;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 25/06/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class HydroClient {

    private HydroangeasServer instancce;
    private UUID uuid;
    private String ip;
    private int maxWeight;
    private Timestamp timestamp;

    private MinecraftServerManager serverManager;

    public HydroClient(HydroangeasServer instancce, UUID uuid)
    {
        this.instancce = instancce;
        this.uuid = uuid;

        this.timestamp = new Timestamp(System.currentTimeMillis());

        serverManager = new MinecraftServerManager(instancce, this);
    }

    public void updateData(HelloFromClientPacket packet)
    {
        if(uuid == null)
            this.uuid = packet.getUUID();

        setIp(packet.getIp());

        setMaxWeight(this.maxWeight = packet.getMaxWeight());

        if(getActualWeight() != packet.getActualWeight())
        {
            instancce.log(Level.SEVERE, "Error client and server not sync about weight! client:" + packet.getActualWeight() + " server:"+ getActualWeight());
        }

        this.timestamp = new Timestamp(System.currentTimeMillis());

    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public String getIp()
    {
        return this.ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public int getMaxWeight()
    {
        return this.maxWeight;
    }

    public void setMaxWeight(int maxWeight)
    {
        this.maxWeight = maxWeight;
    }

    public int getActualWeight()
    {
        return serverManager.getTotalWeight();
    }

    public int getAvailableWeight()
    {
        return getMaxWeight() - getActualWeight();
    }

    public Timestamp getTimestamp()
    {
        return this.timestamp;
    }

    public void setTimestamp(Timestamp timestamp)
    {
        this.timestamp = timestamp;
    }

    public MinecraftServerManager getServerManager()
    {
        return this.serverManager;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof HydroClient)
        {
            if(this.getUUID().equals(((HydroClient)obj).getUUID()))
            {
                return true;
            }
        }
        return false;
    }





}
