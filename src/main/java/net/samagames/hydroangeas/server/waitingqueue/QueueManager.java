package net.samagames.hydroangeas.server.waitingqueue;

import net.samagames.hydroangeas.common.protocol.QueueUpdateFromHub;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.games.BasicGameTemplate;
import net.samagames.hydroangeas.utils.ChatColor;
import net.samagames.hydroangeas.utils.PlayerMessager;

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
            Queue queue = null;

            if(packet.getTypeQueue().equals(QueueUpdateFromHub.TypeQueue.NAMED))
            {
                queue = instance.getQueueManager().getQueueByName(packet.getGame() + "_" + packet.getMap());
                if(queue == null)
                {
                    //error no queue
                    PlayerMessager.sendMessage(packet.getGroupLeader().getUUID(), ChatColor.RED + "Aucun template disponible pour ce jeu!");
                    return;
                }

            }else if(packet.getTypeQueue().equals(QueueUpdateFromHub.TypeQueue.FAST))
            {
                //TODO select best queue
            }else
            {
                //RANDOM
                List<Queue> queuesByGame = instance.getQueueManager().getQueuesByGame(packet.getGame());
                queue = queuesByGame.get(new Random().nextInt(queuesByGame.size()));
            }

            Queue leaderQueue = getQueueByLeader(packet.getGroupLeader().getUUID());
            if(leaderQueue != null)
            {
                if(!leaderQueue.getGame().equals(packet.getGame()) || !leaderQueue.getMap().equals(packet.getMap()))
                {
                    //Error already in queue
                    leaderQueue.removeQPlayer(packet.getGroupLeader());
                    PlayerMessager.sendMessage(packet.getGroupLeader().getUUID(), ChatColor.YELLOW + "Vous quittez la queue " + leaderQueue.getName());
                    //PlayerMessager.sendMessage(packet.getGroupLeader().getUUID(), ChatColor.RED + "Vous êtes déja dans une queue!");
                    return;
                }
            }


            QGroup group = queue.getGroupByLeader(packet.getGroupLeader().getUUID());
            if(group == null)
            {
                queue.addPlayersInNewGroup(packet.getGroupLeader(), packet.getPlayers());

                for(QPlayer qPlayer : packet.getPlayers())
                {
                    PlayerMessager.sendMessage(qPlayer.getUUID(), ChatColor.GREEN + "Vous avez été ajouté à la queue " + ChatColor.RED + queue.getMap());
                }
            }else{
                for(QPlayer player : packet.getPlayers())
                {
                    if(getQueueByPlayer(player.getUUID()) != null)
                    {
                        PlayerMessager.sendMessage(packet.getGroupLeader().getUUID(), ChatColor.RED + "Vous êtes déja dans une queue!");
                        continue;
                    }
                    group.addPlayer(player);
                    PlayerMessager.sendMessage(player.getUUID(), ChatColor.GREEN + "Vous avez été ajouté à la queue " + ChatColor.RED + queue.getMap());
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

                PlayerMessager.sendMessage(player.getUUID(), "Vous avez été retiré de la queue " + queue.getMap());
                //TODO notify removed from queue
            }
        }
    }

    /*public Queue addQueue(String game, String map)
    {
        return this.addQueue(game + "_" + map);
    }*/

    public Queue addQueue(BasicGameTemplate template)
    {
        Queue queue = new Queue(this, template);
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
        queue.remove();
        return queues.remove(queue);
    }

    public void disable()
    {
        queues.forEach(net.samagames.hydroangeas.server.waitingqueue.Queue::remove);
    }

    public Queue getQueueByName(String name)
    {
        for(Queue queue : queues)
        {
            if(queue.getName().equalsIgnoreCase(name))
            {
                return queue;
            }
        }
        return null;
    }

    public List<Queue> getQueuesByGame(String game)
    {
        return queues.stream().filter(queue -> queue.getGame().equalsIgnoreCase(game)).collect(Collectors.toList());
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

    public Queue getQueueByLeader(UUID player)
    {
        for(Queue queue : queues)
        {
            if(queue.containsLeader(player))
            {
                return queue;
            }
        }
        return null;
    }

}
