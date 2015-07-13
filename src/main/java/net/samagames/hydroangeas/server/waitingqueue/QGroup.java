package net.samagames.hydroangeas.server.waitingqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 13/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class QGroup {

    private List<QPlayer> players = new ArrayList<>();

    private int priority;

    public QGroup(QPlayer player)
    {
        players.add(player);
        priority = player.getPriority();
    }

    public QGroup(List<QPlayer> players)
    {
        this.players.addAll(players);
        //Celui qui a la plus grosse prioritée la donne au groupe
        for(QPlayer qPlayer : players)
        {
            priority = Math.max(qPlayer.getPriority(), priority);
        }
    }

    public int getPriority()
    {
        return priority;
    }

    public boolean contains(UUID uuid)
    {
        for(QPlayer qp : players)
        {
            if(qp.getUUID().equals(qp.getUUID()))
            {
                return true;
            }
        }
        return false;
    }

    public boolean contains(QPlayer qp)
    {
        return players.contains(qp);
    }

    public List<UUID> getPlayers()
    {
        return players.stream().map(QPlayer::getUUID).collect(Collectors.toList());
    }

    public List<QPlayer> getQPlayers()
    {
        return players;
    }

    public int getSize()
    {
        return players.size();
    }
}
