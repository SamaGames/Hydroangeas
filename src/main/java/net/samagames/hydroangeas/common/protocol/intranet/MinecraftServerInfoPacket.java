package net.samagames.hydroangeas.common.protocol.intranet;

import com.google.gson.JsonElement;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;
import net.samagames.hydroangeas.common.packets.AbstractPacket;

import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 11/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class MinecraftServerInfoPacket extends AbstractPacket
{

    private UUID uuid;

    private String serverName;
    private UUID serverUUID;
    private int port;

    private Integer hubID;

    private String templateID;
    private boolean coupaingServer;
    private String game;
    private String map;
    private int minSlot;
    private int maxSlot;
    private JsonElement options, startupOptions;

    private int weight;

    private long timeToLive = 14400000L;
    private long startedTime;

    public MinecraftServerInfoPacket()
    {
    }

    public MinecraftServerInfoPacket(Hydroangeas instance, MinecraftServerC server)
    {
        this(instance.getUUID(),
                server.getServerName(),
                server.getUUID(),
                server.getGame(),
                server.getMap(),
                server.getTemplateID(),
                server.getMinSlot(),
                server.getMaxSlot(),
                server.isCoupaingServer(),
                server.getOptions(),
                server.getPort(),
                server.getWeight(),
                server.getTimeToLive(),
                server.getStartedTime(),
                server.getHubID());
    }

    public MinecraftServerInfoPacket(UUID uuid,
                                     String serverName,
                                     UUID serverUUID,
                                     String game,
                                     String map,
                                     String templateID,
                                     int minSlot,
                                     int maxSlot,
                                     boolean coupaingServer,
                                     JsonElement options,
                                     int port,
                                     int weight,
                                     long timeToLive,
                                     long startedTime,
                                     Integer hubID)
    {
        this.uuid = uuid;
        this.serverName = serverName;
        this.serverUUID = serverUUID;
        this.templateID = templateID;
        this.coupaingServer = coupaingServer;
        this.game = game;
        this.map = map;
        this.minSlot = minSlot;
        this.maxSlot = maxSlot;
        this.port = port;
        this.weight = weight;

        this.options = options;
        this.timeToLive = timeToLive;
        this.startedTime = startedTime;
        this.hubID = hubID;
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public boolean isCoupaingServer()
    {
        return coupaingServer;
    }

    public boolean isHub()
    {
        return hubID != null;
    }

    public Integer getHubID()
    {
        return hubID;
    }

    public String getGame()
    {
        return game;
    }

    public String getMap()
    {
        return map;
    }

    public int getMinSlot()
    {
        return minSlot;
    }

    public int getMaxSlot()
    {
        return maxSlot;
    }

    public JsonElement getOptions()
    {
        return options;
    }

    public String getServerName()
    {
        return serverName;
    }

    public int getPort()
    {
        return port;
    }

    public int getWeight()
    {
        return weight;
    }

    public UUID getServerUUID()
    {
        return serverUUID;
    }

    public JsonElement getStartupOptions()
    {
        return startupOptions;
    }

    public String getTemplateID() {
        return templateID;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public long getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(long startedTime) {
        this.startedTime = startedTime;
    }
}
