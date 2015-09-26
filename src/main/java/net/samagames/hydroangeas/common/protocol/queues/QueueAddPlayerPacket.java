package net.samagames.hydroangeas.common.protocol.queues;

import net.samagames.hydroangeas.server.waitingqueue.QPlayer;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 24/08/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class QueueAddPlayerPacket extends QueuePacket
{

    private QPlayer player;

    public QueueAddPlayerPacket()
    {
    }

    public QueueAddPlayerPacket(TypeQueue typeQueue, String game, String map, QPlayer player)
    {
        super(typeQueue, game, map);

        this.player = player;
    }

    public QueueAddPlayerPacket(TypeQueue typeQueue, String templateID, QPlayer player)
    {
        super(typeQueue, templateID);

        this.player = player;
    }

    public QPlayer getPlayer()
    {
        return player;
    }
}
