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
    private String queueName;

    private PriorityBlockingQueue<QGroup> queue;

    public Queue(QueueManager manager, String queueName, int size)
    {
        this.manager = manager;
        this.queueName = queueName;

        //Si priority plus grande alors tu passe devant.
        this.queue = new PriorityBlockingQueue<>(size, (o1, o2) -> -Integer.compare(o1.getPriority(), o2.getPriority()));
    }

    public boolean addPlayers(List<QPlayer> players)
    {
        return addPlayer(new QGroup(players));
    }

    public boolean addPlayer(QGroup qGroup)
    {
        return queue.add(qGroup);
    }

    public boolean removePlayer(QGroup qGroup)
    {
        return queue.remove(qGroup);
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
        return queue.size();
    }

    public String getName() {
        return queueName;
    }
}
