package net.samagames.hydroangeas.server.client;

import java.util.HashMap;
import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 25/06/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class MinecraftServerS {

    private UUID uuid;
    private boolean coupaingServer;
    private String game;
    private String map;
    private int minSlot;
    private int maxSlot;
    private HashMap<String, String> options;

    private boolean started;

    public MinecraftServerS(String game, String map)
    {
        this.uuid = UUID.randomUUID();
        this.game = game;
        this.map = map;
        this.minSlot = 0;
        this.maxSlot = 0;
        this.options = null;

        this.coupaingServer = false;
    }

    public MinecraftServerS(String game, String map, int minSlot, int maxSlot, HashMap<String, String> options)
    {
        this.uuid = UUID.randomUUID();
        this.game = game;
        this.map = map;
        this.minSlot = minSlot;
        this.maxSlot = maxSlot;
        this.options = options;

        this.coupaingServer = true;
    }

    public void shutdown()
    {
        //If we need to save some data after shutdown
    }

    public void changeUUID()
    {
        this.uuid = UUID.randomUUID();
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
        return this.coupaingServer;
    }

    public void setCoupaingServer(boolean isCoupaing)
    {
        this.coupaingServer = isCoupaing;
    }

    public boolean isStarted()
    {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}
