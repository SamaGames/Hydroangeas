package net.samagames.hydroangeas.common.protocol;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.common.packets.AbstractPacket;

import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 01/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */

public class MinecraftServerUpdatePacket extends AbstractPacket {

    private UType action;

    private UUID uuid;
    private String serverName;

    private int newWeight;
    private int maxWeight;


    public MinecraftServerUpdatePacket(HydroangeasClient instance, String serverName, UType action)
    {
        this.uuid = instance.getClientUUID();
        this.newWeight = instance.getActualWeight();
        this.maxWeight = instance.getMaxWeight();

        this.serverName = serverName;
        this.action = action;
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public String getServerName()
    {
        return serverName;
    }

    public UType getAction()
    {
        return action;
    }

    public int getNewWeight()
    {
        return newWeight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }


    public enum UType {START, INFO, END}
}
