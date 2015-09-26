package net.samagames.hydroangeas.common.protocol.intranet;

import com.google.gson.JsonElement;
import net.samagames.hydroangeas.common.packets.AbstractPacket;
import net.samagames.hydroangeas.server.client.MinecraftServerS;

import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 25/06/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class MinecraftServerOrderPacket extends AbstractPacket
{

    private UUID uuid;

    private String game;
    private String map;

    private int minSlot;
    private int maxSlot;

    private boolean isCoupaing = true;

    private JsonElement options;

    public MinecraftServerOrderPacket(MinecraftServerS server)
    {
        this(server.getUUID(), server.getGame(), server.getMap(), server.getMinSlot(), server.getMaxSlot(), server.getOptions());
    }

    public MinecraftServerOrderPacket(UUID uuid, String game, String map, int minSlot, int maxSlot, JsonElement options)
    {
        this.uuid = uuid;
        this.game = game;
        this.map = map;
        this.minSlot = minSlot;
        this.maxSlot = maxSlot;
        this.options = options;
    }

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

    public JsonElement getOptions()
    {
        return this.options;
    }

    public boolean isCoupaingServer()
    {
        return this.isCoupaing;
    }
}
