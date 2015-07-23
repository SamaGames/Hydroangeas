package net.samagames.hydroangeas.server.waitingqueue;

import net.samagames.hydroangeas.common.protocol.QueueUpdateFromHub;
import net.samagames.hydroangeas.server.HydroangeasServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 12/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class QueueManager {

    public List<Queue> queues = new ArrayList<>();
    private HydroangeasServer instance;

    public QueueManager(HydroangeasServer instance)
    {

        this.instance = instance;
    }

    public void handlepacket(QueueUpdateFromHub packet)
    {
        if(packet.getGroupLeader() == null)
        {
            instance.getLogger().info("Received queue modify packet with no leader !");
        }else if(packet.getAction().equals(QueueUpdateFromHub.ActionQueue.ADD))
        {
            Queue queue = getQueueByPlayer(packet.getGroupLeader().getUUID());
            if(queue != null)
            {
                if(!queue.getGame().equals(packet.getGame()) || !queue.getMap().equals(packet.getMap()))
                {
                    //Error already in queue
                    //TODO notify player
                    return;
                }
            }
            else if(packet.getTypeQueue().equals(QueueUpdateFromHub.TypeQueue.NAMED))
            {
                queue = instance.getQueueManager().getQueueByName(packet.getGame() + "_" + packet.getMap());
            }else if(packet.getTypeQueue().equals(QueueUpdateFromHub.TypeQueue.RANDOM))
            {
                List<Queue> queuesByGame = instance.getQueueManager().getQueuesByGame(packet.getGame());
                queue = queuesByGame.get(new Random().nextInt(queuesByGame.size()));
            }else if(packet.getTypeQueue().equals(QueueUpdateFromHub.TypeQueue.FAST))
            {
                //TODO select best queue
            }

            QGroup group = queue.getGroupByLeader(packet.getGroupLeader().getUUID());
            if(group == null)
            {
                queue.addPlayersInNewGroup(packet.getGroupLeader(), packet.getPlayers());

                //TODO notify players
            }else{
                for(QPlayer player : packet.getPlayers())
                {
                    group.addPlayer(player);
                    //TODO Check if present in an other queue
                    //TODO notify players
                }
            }
        }else if(packet.getAction().equals(QueueUpdateFromHub.ActionQueue.REMOVE))
        {
            Queue queue = instance.getQueueManager().getQueueByPlayer(packet.getGroupLeader().getUUID());
            if(queue == null)
            {
                return;
            }
            //TODO add if necessary
            /*if(!queue.getGame().equals(packet.getGame()) || !queue.getMap().equals(packet.getMap()))
            {
                instance.getLogger().info("Problem in queues. Tried to delete players in queue but queue .");
            }*/
            for(QPlayer player : packet.getPlayers())
            {
                queue.removeQPlayer(player);
                //TODO notify removed from queue
            }
        }
    }

    public Queue addQueue(String name)
    {
        Queue queue = new Queue(this, name);
        queues.add(queue);
        return queue;
    }

    public boolean removeQueue(String name)
    {
        Queue queue = getQueueByName(name);
        if(queue == null)
        {
            return false;
        }
        return removeQueue(queue);
    }

    public boolean removeQueue(Queue queue)
    {
        return queues.remove(queue);
    }

    public Queue getQueueByName(String name)
    {
        for(Queue queue : queues)
        {
            if(queue.getName().equals(name))
            {
                return queue;
            }
        }
        return null;
    }

    public List<Queue> getQueuesByGame(String game)
    {
        return queues.stream().filter(queue -> queue.getGame().equals(game)).collect(Collectors.toList());
    }

    public Queue getQueueByPlayer(UUID player)
    {
        for(Queue queue : queues)
        {
            if(queue.containsUUID(player))
            {
                return queue;
            }
        }
        return null;
    }

}
