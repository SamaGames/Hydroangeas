package net.samagames.hydroangeas.common.protocol;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.common.packets.AbstractPacket;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 25/06/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class HelloFromClientPacket extends AbstractPacket{

    private UUID uuid;
    private String ip;
    private int maxWeight;
    private int actualWeight;
    private Timestamp timestamp;

    public HelloFromClientPacket(HydroangeasClient instance)
    {
        this(instance.getClientUUID(), instance.getIP(), instance.getMaxWeight(), instance.getActualWeight(), new Timestamp(System.currentTimeMillis()));
    }

    public HelloFromClientPacket(UUID uuid, String ip, int maxWeight, int actualWeight, Timestamp timestamp)
    {
        this.uuid = uuid;
        this.ip = ip;
        this.maxWeight = maxWeight;
        this.actualWeight = actualWeight;
        this.timestamp = timestamp;
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public String getIp()
    {
        return ip;
    }

    public int getMaxWeight()
    {
        return maxWeight;
    }

    public int getActualWeight()
    {
        return actualWeight;
    }

    public Timestamp getTimestamp()
    {
        return timestamp;
    }
}
