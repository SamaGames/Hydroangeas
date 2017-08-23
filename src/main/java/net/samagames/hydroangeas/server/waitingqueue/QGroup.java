package net.samagames.hydroangeas.server.waitingqueue;

import net.samagames.hydroangeas.common.samapi.GameConnector;
import net.samagames.hydroangeas.server.client.MinecraftServerS;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/*
 * This file is part of Hydroangeas.
 *
 * Hydroangeas is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hydroangeas is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hydroangeas.  If not, see <http://www.gnu.org/licenses/>.
 */
public class QGroup
{

    private QPlayer leader;

    private List<QPlayer> players = new ArrayList<>();

    private int priority;

    public QGroup(QPlayer player)
    {
        this.leader = player;
        players.add(player);
        calculatePriority();
    }

    public QGroup(List<QPlayer> players)
    {
        this(players.get(0), players);
    }

    public QGroup(QPlayer leader, List<QPlayer> players)
    {
        this.leader = leader;
        priority = leader.getPriority();
        this.players.addAll(players);
        calculatePriority();
    }

    public void calculatePriority()
    {
        //Celui qui a la plus grosse prioritée la donne au groupe
        for (QPlayer qPlayer : players)
        {
            priority = Math.min(qPlayer.getPriority(), priority);
        }
    }

    public int getPriority()
    {
        return priority;
    }

    public boolean contains(UUID uuid)
    {
        for (QPlayer qp : players)
        {
            if (qp.getUUID().equals(uuid))
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

    public QPlayer getPlayerByUUID(UUID player)
    {
        for (QPlayer p : players)
        {
            if (p.getUUID().equals(player))
            {
                return p;
            }
        }
        return null;
    }

    public boolean addPlayer(QPlayer player)
    {
        if (contains(player.getUUID()))
            return false;
        try
        {
            return players.add(player);
        } finally
        {
            calculatePriority();
        }
    }

    public boolean removeQPlayer(UUID player)
    {
        return removeQPlayer(getPlayerByUUID(player));
    }

    public boolean removeQPlayer(QPlayer player)
    {
        if (leader != null && player.getUUID().equals(leader.getUUID()))
            leader = null;

        try
        {
            return players.remove(player);
        } finally
        {
            calculatePriority();
        }
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

    public QPlayer getLeader()
    {
        return leader;
    }

    @Deprecated
    public void sendTo(String serverName)
    {
        for (QPlayer player : players)
        {
            GameConnector.sendPlayerToServer(serverName, player.getUUID());
        }
    }

    public void sendTo(MinecraftServerS serverS)
    {
        for (QPlayer player : players)
        {
            serverS.sendPlayer(player.getUUID());
        }
    }
}
