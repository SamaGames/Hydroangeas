package net.samagames.hydroangeas.server.waitingqueue;

import net.samagames.hydroangeas.utils.PriorityBlockingQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 12/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class Queue {

    private QueueManager manager;

    private String game;
    private String map;

    private PriorityBlockingQueue<QGroup> queue;

    public Queue(QueueManager manager, String name)
    {
        this(manager, name.split("_")[0], name.split("_")[1]);
    }

    public Queue(QueueManager manager, String game, String map)
    {
        this.manager = manager;
        this.game = game;
        this.map = map;

        //Si priority plus grande alors tu passe devant.
        this.queue = new PriorityBlockingQueue<>(Integer.MAX_VALUE, (o1, o2) -> -Integer.compare(o1.getPriority(), o2.getPriority()));
    }

    public boolean addPlayersInNewGroup(QPlayer leader, List<QPlayer> players)
    {
        return addGroup(new QGroup(leader, players));
    }

    public boolean addGroup(QGroup qGroup)
    {
        return queue.add(qGroup);
    }

    public boolean removeGroup(QGroup qGroup)
    {
        return queue.remove(qGroup);
    }

    public boolean removeQPlayer(QPlayer player)
    {
        QGroup group = getGroupByPlayer(player.getUUID());
        boolean result = group.removeQPlayer(player);
        if(group.getLeader() == null)
        {
            removeGroup(group);
        }
        return result;
    }

    //No idea for the name ..
    public List<QGroup> getGroupsListFormatted(int number)
    {
        List<QGroup> data = new ArrayList<>();
        queue.drainTo(data, number);

        return data;
    }

    //No idea for the name ..
    public List<QPlayer> getUserListFormatted(int number)
    {
        List<QPlayer> players = new ArrayList<>();

        getGroupsListFormatted(number).stream().forEachOrdered(qGroup -> players.addAll(qGroup.getQPlayers()));
        return players;
    }

    //No idea for the name ..
    public HashMap<UUID, Integer> getQueueFormated()
    {
        HashMap<UUID, Integer> data = new HashMap<>();
        int i = 0;
        for (QGroup qGroup : queue) {
            for (QPlayer qPlayer : qGroup.getQPlayers())
            {
                data.put(qPlayer.getUUID(), i);
            }
        }
        return data;
    }

    public QGroup getGroupByLeader(UUID leader)
    {
        for(QGroup group : queue)
        {
            if(group.getLeader().getUUID().equals(leader))
            {
                return group;
            }
        }
        return null;
    }

    public QGroup getGroupByPlayer(UUID player)
    {
        for(QGroup group : queue)
        {
            if(group.contains(player))
            {
                return group;
            }
        }
        return null;
    }

    public int getRank(UUID uuid)
    {
        int i = 0;
        for (QGroup qGroup : queue)
        {
            if (qGroup.contains(uuid))
            {
                break;
            }
            i++;
        }
        return i;
    }

    public boolean removeUUID(UUID uuid)
    {
        QGroup group = getGroupByPlayer(uuid);
        if(group == null)
            return false;
        return group.removeQPlayer(uuid);
    }

    public boolean containsUUID(UUID uuid)
    {
        for(QGroup qGroup : queue)
        {
            if(qGroup.contains(uuid))
            {
                return true;
            }
        }
        return false;
    }

    public int getSize()
    {
        int i = 0;
        for(QGroup group : queue)
        {
            i += group.getSize();
        }
        return i;
    }

    public String getName()
    {
        return game + "_" + map;
    }

    public String getGame() {
        return game;
    }

    public String getMap() {
        return map;
    }
}
