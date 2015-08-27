package net.samagames.hydroangeas.server.waitingqueue;

import net.samagames.hydroangeas.common.protocol.queues.*;
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

    public void handlepacket(QueueAddPlayerPacket packet)
    {

        QPlayer player = packet.getPlayer();
        Queue designedQueue = getQueue(packet);
        Queue currentQueue = getQueueByLeader(player.getUUID());

        if(designedQueue == null)
        {
            //No queue to add get out !
            PlayerMessager.sendMessage(player.getUUID(), ChatColor.RED + "Aucun template disponible pour ce jeu!");
            return;
        }

        if(designedQueue.equals(currentQueue))
        {
            PlayerMessager.sendMessage(player.getUUID(), player.getUUID().toString());
            PlayerMessager.sendMessage(player.getUUID(), ChatColor.GREEN + "Tu es déjà dans la queue!");
            return;
        }

        if(currentQueue != null)
        {
            currentQueue.removeQPlayer(player);
        }

        ArrayList<QPlayer> players = new ArrayList<>();
        players.add(player);
        addPlayerToQueue(designedQueue, player, players);
    }

    public void handlepacket(QueueRemovePlayerPacket packet)
    {
        QPlayer player = packet.getPlayer();
        //Queue designedQueue = getQueue(packet);
        Queue currentQueue = getQueueByPlayer(player.getUUID());

        /*if(designedQueue == null)
        {
            //No queue maybe not known ?
            //PlayerMessager.sendMessage(player.getUUID(), ChatColor.RED + "Aucun queue définis!");
        }*/
        if(currentQueue == null)
        {
            //No queue, security remove ?
            PlayerMessager.sendMessage(player.getUUID(), ChatColor.RED + "Vous n'êtes dans aucune queue!");
            return;
        }

        currentQueue.removeQPlayer(player);
        PlayerMessager.sendMessage(player.getUUID(), ChatColor.YELLOW + "Vous quittez la queue " + currentQueue.getName());
    }

    public void handlepacket(QueueAttachPlayerPacket packet)
    {
        QPlayer leader = packet.getLeader();
        Queue designedQueue = getQueueByLeader(leader.getUUID());

        if(designedQueue == null)
        {
            /*for(QPlayer qPlayer : packet.getPlayers())
            {
                PlayerMessager.sendMessage(qPlayer.getUUID(), ChatColor.RED + "Le leader de votre party n'a pas choisit de queue !");
            }*/
            return;
        }

        QGroup group = designedQueue.getGroupByLeader(leader.getUUID());

        if(group == null)
        {
            //Logically not possible but.. #MOJANG

            for(QPlayer qPlayer : packet.getPlayers())
            {
                PlayerMessager.sendMessage(qPlayer.getUUID(), ChatColor.RED + "Le leader de votre party n'a pas choisit de queue !");
            }
            return;
        }
        designedQueue.removeGroup(group);
        packet.getPlayers().forEach(group::addPlayer);
        designedQueue.addGroup(group);
    }

    public void handlepacket(QueueDetachPlayerPacket packet)
    {
        for(QPlayer player : packet.getPlayers())
        {
            Queue designedQueue = getQueueByPlayer(player.getUUID());
            if(designedQueue != null)
            {
                designedQueue.removeQPlayer(player);

                PlayerMessager.sendMessage(player.getUUID(), "Vous avez été retiré de la queue " + designedQueue.getMap());
            }
        }
    }

    public Queue getQueue(QueuePacket packet)
    {
        Queue queue = null;
        QueuePacket.TypeQueue typeQueue = packet.getTypeQueue();

        try{
            if(typeQueue.equals(QueuePacket.TypeQueue.NAMED))
            {
                queue = instance.getQueueManager().getQueueByName(packet.getGame() + "_" + packet.getMap());
            }else if(typeQueue.equals(QueuePacket.TypeQueue.NAMEDID))
            {
                queue = instance.getQueueManager().getQueueByName(packet.getTemplateID());
            }else if(typeQueue.equals(QueuePacket.TypeQueue.FAST))
            {
                //TODO select best queue
            }else
            {
                //RANDOM
                List<Queue> queuesByGame = instance.getQueueManager().getQueuesByGame(packet.getGame());
                queue = queuesByGame.get(new Random().nextInt(queuesByGame.size()));
            }

            /*if(queue == null)
            {
                //error no queue
                //PlayerMessager.sendMessage(, ChatColor.RED + "Aucun template disponible pour ce jeu!");
                return null;
            }*/
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return queue;
    }

   /* public void handlepacket(QueueUpdateFromHub packet)
    {
        if(packet.getGroupLeader() == null)
        {
            instance.getLogger().info("Received queue modify packet with no leader !");
        }else if(packet.getAction().equals(QueueUpdateFromHub.ActionQueue.ADD))
        {

            Queue leaderQueue = getQueueByLeader(packet.getGroupLeader().getUUID());
            if(leaderQueue != null)
            {
                if(!leaderQueue.getGame().equals(packet.getGame()) || !leaderQueue.getMap().equals(packet.getMap()))
                {
                    //Error already in queue
                    leaderQueue.removeQPlayer(packet.getGroupLeader());
                    PlayerMessager.sendMessage(packet.getGroupLeader().getUUID(), ChatColor.YELLOW + "Vous quittez la queue " + leaderQueue.getName());
                    //PlayerMessager.sendMessage(packet.getGroupLeader().getUUID(), ChatColor.RED + "Vous êtes déja dans une queue!");
                    if(queue != null)
                    {
                        addPlayerToQueue(queue, packet.getGroupLeader(), packet.getPlayers());
                    }
                    return;
                }else{
                    queue = leaderQueue;
                }
            }

            QGroup group = queue.getGroupByLeader(packet.getGroupLeader().getUUID());
            if(group == null)
            {
                addPlayerToQueue(queue, packet.getGroupLeader(), packet.getPlayers());
            }else{
                for(QPlayer player : packet.getPlayers())
                {
                    Queue queue1 = null;
                    if((queue1 = getQueueByPlayer(player.getUUID())) != null)
                    {
                        queue1.removeQPlayer(player);
                        //PlayerMessager.sendMessage(packet.getGroupLeader().getUUID(), ChatColor.RED + "Vous êtes déja dans une queue!");
                        //continue;
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
            }*/ /*
            for(QPlayer player : packet.getPlayers())
            {
                queue.removeQPlayer(player);

                PlayerMessager.sendMessage(player.getUUID(), "Vous avez été retiré de la queue " + queue.getMap());
                //TODO notify removed from queue
            }
        }
    }*/

    /*public Queue addQueue(String game, String map)
    {
        return this.addQueue(game + "_" + map);
    }*/

    public void addPlayerToQueue(Queue queue, QPlayer leader, List<QPlayer> players)
    {
        queue.addPlayersInNewGroup(leader, players);

        for(QPlayer qPlayer : players)
        {
            PlayerMessager.sendMessage(qPlayer.getUUID(), ChatColor.GREEN + "Vous avez été ajouté à la queue " + ChatColor.RED + queue.getMap());
        }
    }

    public void removePlayerFromQueue(Queue queue, QPlayer leader, List<QPlayer> players)
    {
        //TODO just do it !!!!!
    }

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
