package net.samagames.hydroangeas.common.protocol.hubinfo;

import net.samagames.hydroangeas.common.packets.AbstractPacket;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 01/09/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class GameInfosToHubPacket extends AbstractPacket
{

    private int playerMaxForMap;
    private int playerWaitFor;
    private int totalPlayerOnServers;

    private String templateID;

    public GameInfosToHubPacket()
    {
    }

    public GameInfosToHubPacket(String templateID)
    {
        this.templateID = templateID;
    }

    public int getPlayerMaxForMap()
    {
        return playerMaxForMap;
    }

    public void setPlayerMaxForMap(int playerMaxForMap)
    {
        this.playerMaxForMap = playerMaxForMap;
    }

    public int getPlayerWaitFor()
    {
        return playerWaitFor;
    }

    public void setPlayerWaitFor(int playerWaitFor)
    {
        this.playerWaitFor = playerWaitFor;
    }

    public int getTotalPlayerOnServers()
    {
        return totalPlayerOnServers;
    }

    public void setTotalPlayerOnServers(int totalPlayerOnServers)
    {
        this.totalPlayerOnServers = totalPlayerOnServers;
    }

    public String getTemplateID()
    {
        return templateID;
    }
}
