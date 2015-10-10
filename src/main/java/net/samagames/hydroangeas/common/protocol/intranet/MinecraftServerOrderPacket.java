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

    private Integer hubID;
    private String game;
    private String map;

    private int minSlot;
    private int maxSlot;

    private String templateID;

    private boolean isCoupaing = true;

    private JsonElement options, startupOptions;
    private int weight;

    public MinecraftServerOrderPacket(MinecraftServerS server)
    {
        this(server.getUUID(), server.getHubID(), server.getGame(), server.getMap(), server.getTemplateID(), server.getMinSlot(), server.getMaxSlot(), server.getOptions(), server.getStartupOptions(), server.getWeight());
    }

    public MinecraftServerOrderPacket(UUID uuid, Integer hubID, String game, String map, String templateID, int minSlot, int maxSlot, JsonElement options, JsonElement startupOptions, int weight)
    {
        this.uuid = uuid;
        this.hubID = hubID;
        this.game = game;
        this.map = map;
        this.templateID = templateID;
        this.minSlot = minSlot;
        this.maxSlot = maxSlot;
        this.options = options;
        this.startupOptions = startupOptions;
        this.weight = weight;
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
        return this.game + "_" + ((hubID == null) ? this.uuid.toString().split("-")[0] : hubID);
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

    public Integer getHubID()
    {
        return hubID;
    }

    public JsonElement getStartupOptions()
    {
        return startupOptions;
    }

    public int getWeight()
    {
        return weight;
    }

    public String getTemplateID() {
        return templateID;
    }
}
