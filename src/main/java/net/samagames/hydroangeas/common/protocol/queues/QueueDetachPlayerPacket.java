package net.samagames.hydroangeas.common.protocol.queues;

import net.samagames.hydroangeas.server.waitingqueue.QPlayer;

import java.util.List;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 24/08/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class QueueDetachPlayerPacket extends QueuePacket
{

    private QPlayer leader;
    private List<QPlayer> players;

    public QueueDetachPlayerPacket()
    {
    }

    public QueueDetachPlayerPacket(QPlayer leader, List<QPlayer> players)
    {
        this.leader = leader;
        this.players = players;
    }

    public QPlayer getLeader()
    {
        return leader;
    }

    public List<QPlayer> getPlayers()
    {
        return players;
    }
}
