package net.samagames.hydroangeas.common.informations;

import java.util.HashMap;
import java.util.UUID;

public class MinecraftServerInfos
{
    private final UUID uuid;
    private final boolean coupaingServer;
    private String game;
    private String map;
    private int minSlot;
    private int maxSlot;
    private HashMap<String, String> options;

    public MinecraftServerInfos(String game, String map)
    {
        this.uuid = UUID.randomUUID();
        this.game = game;
        this.map = map;

        this.coupaingServer = false;
    }

    public MinecraftServerInfos(String game, String map, int minSlot, int maxSlot, HashMap<String, String> options)
    {
        this.uuid = UUID.randomUUID();
        this.game = game;
        this.map = map;
        this.minSlot = minSlot;
        this.maxSlot = maxSlot;
        this.options = options;

        this.coupaingServer = true;
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
}
