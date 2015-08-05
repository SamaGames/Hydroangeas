package net.samagames.hydroangeas.server.waitingqueue;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.server.data.Status;
import net.samagames.hydroangeas.server.games.BasicGameTemplate;
import net.samagames.hydroangeas.utils.PriorityBlockingQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private CopyOnWriteArrayList<MinecraftServerS> waitingServers;

    private Thread worker;
    private boolean working = true;

    public Queue(QueueManager manager, String name)
    {
        this(manager, name.split("_")[0], name.split("_")[1]);
    }

    public Queue(QueueManager manager, String game, String map)
    {
        this.manager = manager;
        this.game = game;
        this.map = map;

        this.waitingServers = new CopyOnWriteArrayList<>();

        //Si priority plus grande alors tu passe devant.
        this.queue = new PriorityBlockingQueue<>(10000, (o1, o2) -> -Integer.compare(o1.getPriority(), o2.getPriority()));

        worker = new Thread(() -> {
            while (working)
            {
                BasicGameTemplate template = getTemplate();
                if(template == null)
                {
                    Hydroangeas.getInstance().getLogger().info("Template null!");
                    continue;
                }

                for(MinecraftServerS serverS : waitingServers)
                {
                    if(serverS.getStatus().isAllowJoin())
                    {
                        List<QGroup> groups = new ArrayList<>();
                        queue.drainTo(groups, serverS.getMaxSlot());
                        for(QGroup group : groups)
                        {
                            group.sendTo(serverS.getServerName());
                        }
                    }
                    if(serverS.getStatus().equals(Status.IN_GAME) || serverS.getActualSlots() >= serverS.getMaxSlot() * 0.90)
                    {
                        waitingServers.remove(serverS);
                    }
                }

                if(waitingServers.size() <= 0 && queue.size() >= template.getMaxSlot() * 0.7)
                {
                    MinecraftServerS serverS = Hydroangeas.getInstance().getAsServer().getAlgorithmicMachine().orderTemplate(template);
                    waitingServers.add(serverS);
                }

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, game + map + " Worker");
        worker.start();
    }

    public void remove()
    {
        working = false;
        worker.interrupt();
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
        for(QGroup qGroup : queue)
        {
            if(qGroup == null)
                continue;
            if(qGroup.getLeader().getUUID().equals(leader))
            {
                return qGroup;
            }
        }
        return null;
    }

    public QGroup getGroupByPlayer(UUID player)
    {
        for(QGroup qGroup : queue)
        {
            if(qGroup == null)
                continue;
            if(qGroup.contains(player))
            {
                return qGroup;
            }
        }
        return null;
    }

    public int getRank(UUID uuid)
    {
        int i = 0;
        for (QGroup qGroup : queue)
        {
            if(qGroup == null)
                continue;
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
            if(qGroup == null)
                continue;
            if(qGroup.contains(uuid))
            {
                return true;
            }
        }
        return false;
    }

    public boolean containsLeader(UUID uuid)
    {
        for(QGroup qGroup : queue)
        {
            if(qGroup == null)
                continue;
            if(qGroup.getLeader().getUUID().equals(uuid))
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

    public BasicGameTemplate getTemplate()
    {
        return Hydroangeas.getInstance().getAsServer().getAlgorithmicMachine().getTemplateByGameAndMap(game, map);
    }
}
