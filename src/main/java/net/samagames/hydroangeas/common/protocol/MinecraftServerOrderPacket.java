package net.samagames.hydroangeas.common.protocol;

import net.samagames.hydroangeas.common.packets.AbstractPacket;

import java.util.HashMap;
import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 25/06/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class MinecraftServerOrderPacket extends AbstractPacket{

    public UUID uuid;

    public String game;
    public String map;

    public int minSlot;
    public int maxSlot;

    public boolean isCoupaing = true;

    public HashMap<String, String> options;

    public MinecraftServerOrderPacket()
    {

    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public String getGame()
    {
        return this.game;
    }

    public String getMap()
    {
        return this.map;
    }

    public String getServerName()
    {
        return this.game + "_" + this.uuid.toString().split("-")[0];
    }

    public int getMinSlot()
    {
        return this.minSlot;
    }

    public int getMaxSlot()
    {
        return this.maxSlot;
    }

    public HashMap<String, String> getOptions()
    {
        return this.options;
    }

    public boolean isCoupaingServer()
    {
        return this.isCoupaing;
    }
}
