package net.samagames.hydroangeas.common.informations;

import java.util.UUID;

public class MinecraftServerInfos
{
    private final UUID uuid;
    private final String game;
    private final String map;

    public MinecraftServerInfos(String game, String map)
    {
        this.uuid = UUID.randomUUID();
        this.game = game;
        this.map = map;
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
}
