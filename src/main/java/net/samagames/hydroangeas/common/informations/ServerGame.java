package net.samagames.hydroangeas.common.informations;

public class ServerGame
{
    private final String game;
    private final String map;
    private final int instances;

    public ServerGame(String game, String map, int instances)
    {
        this.game = game;
        this.map = map;
        this.instances = instances;
    }

    public String getGame()
    {
        return this.game;
    }

    public String getMap()
    {
        return this.map;
    }

    public int getInstances()
    {
        return this.instances;
    }
}
