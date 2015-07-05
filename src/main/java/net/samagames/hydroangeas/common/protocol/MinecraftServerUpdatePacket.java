package net.samagames.hydroangeas.common.protocol;

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

    public UType action;

    public UUID uuid;
    public String serverName;



    public MinecraftServerUpdatePacket(UUID uuid, String serverName, UType action)
    {
        this.uuid = uuid;
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


    public enum UType { MAKE, PATCH, START, STOP, DELETE }
}
