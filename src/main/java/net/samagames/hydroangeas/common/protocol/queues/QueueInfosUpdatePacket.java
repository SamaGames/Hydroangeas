package net.samagames.hydroangeas.common.protocol.queues;

import net.samagames.hydroangeas.common.packets.AbstractPacket;
import net.samagames.hydroangeas.server.waitingqueue.QPlayer;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 24/08/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class QueueInfosUpdatePacket extends AbstractPacket
{

    private Type type;
    private boolean success;
    private String errorMessage;

    private String game;
    private String map;

    private QPlayer player;

    public QueueInfosUpdatePacket()
    {
    }

    public QueueInfosUpdatePacket(QPlayer player, Type type, boolean success, String errorMessage)
    {
        this.player = player;
        this.type = type;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public QueueInfosUpdatePacket(QPlayer player, Type type, String game, String map)
    {
        this.player = player;
        this.type = type;
        this.game = game;
        this.map = map;
    }

    public Type getType()
    {
        return type;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public String getGame()
    {
        return game;
    }

    public String getMap()
    {
        return map;
    }

    public QPlayer getPlayer()
    {
        return player;
    }


    public enum Type
    {
        ADD, REMOVE
    }
}
