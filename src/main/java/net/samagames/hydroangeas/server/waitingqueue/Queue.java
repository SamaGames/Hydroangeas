package net.samagames.hydroangeas.server.waitingqueue;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.protocol.hubinfo.GameInfosToHubPacket;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.server.games.AbstractGameTemplate;
import net.samagames.hydroangeas.server.games.PackageGameTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 12/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class Queue
{

    private final ScheduledFuture workerTask, hubRefreshTask;
    private final String map;
    private QueueManager manager;

    private HydroangeasServer instance;

    private String game;

    private PriorityPlayerQueue queue;
    private volatile boolean sendInfo = true;

    private AbstractGameTemplate template;

    private long lastSend = System.currentTimeMillis();

    public Queue(QueueManager manager, AbstractGameTemplate template)
    {
        this.instance = Hydroangeas.getInstance().getAsServer();
        this.manager = manager;
        this.template = template;
        this.game = template.getGameName();
        this.map = template.getMapName();

        if(template instanceof PackageGameTemplate)//Assuming that the package template have not selected a template we force it
        {
            ((PackageGameTemplate) template).selectTemplate();
        }

        //Si priority plus grande alors tu passe devant.
        this.queue = new PriorityPlayerQueue(100000, (o1, o2) -> -Integer.compare(o1.getPriority(), o2.getPriority()));

        workerTask = instance.getScheduler().scheduleAtFixedRate(() ->
        {
            if (template == null)
            {
                Hydroangeas.getInstance().getLogger().info("Template null!");
                return;
            }

            List<MinecraftServerS> servers = instance.getAlgorithmicMachine().getServerByTemplatesAndAvailable(template.getId());

            servers.stream().filter(server -> server.getStatus().isAllowJoin()).forEach(server -> {
                List<QGroup> groups = new ArrayList<>();
                queue.drainTo(groups, server.getMaxSlot());
                for(QGroup group : groups)
                {
                    group.sendTo(server.getServerName());
                }
            });

            if(servers.size() <= 0 && queue.size() >= template.getMinSlot())
            {
                Hydroangeas.getInstance().getAsServer().getAlgorithmicMachine().orderTemplate(template);
                if(template instanceof PackageGameTemplate) // If it's a package template we change it now
                {
                    ((PackageGameTemplate) template).selectTemplate();
                }
            }
        }, 0, 900, TimeUnit.MILLISECONDS);
        hubRefreshTask = instance.getScheduler().scheduleAtFixedRate(this::sendInfoToHub, 0, 750, TimeUnit.MILLISECONDS);

    }

    public void remove()
    {
        workerTask.cancel(true);
        hubRefreshTask.cancel(true);
    }

    public boolean addPlayersInNewGroup(QPlayer leader, List<QPlayer> players)
    {
        return addGroup(new QGroup(leader, players));
    }

    public boolean addGroup(QGroup qGroup)
    {
        try
        {
            return queue.add(qGroup);
        } finally
        {
            sendInfo = true;
        }
    }

    public boolean removeGroup(QGroup qGroup)
    {
        try
        {
            return queue.remove(qGroup);
        } finally
        {
            sendInfo = true;
        }
    }

    public boolean removeQPlayer(QPlayer player)
    {
        try
        {
            QGroup group = getGroupByPlayer(player.getUUID());
            boolean result = group.removeQPlayer(player);
            removeGroup(group);
            if (group.getLeader() != null)
            {
                addGroup(group);
            }
            return result;
        } finally
        {
            sendInfo = true;
        }
    }

    //No idea for the name ..
    public List<QGroup> getGroupsListFormatted(int number)
    {
        List<QGroup> data = new ArrayList<>();
        queue.drainPlayerTo(data, number);

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
        for (QGroup qGroup : queue)
        {
            for (QPlayer qPlayer : qGroup.getQPlayers())
            {
                data.put(qPlayer.getUUID(), i);
            }
        }
        return data;
    }

    public QGroup getGroupByLeader(UUID leader)
    {
        for (QGroup qGroup : queue)
        {
            if (qGroup == null)
                continue;
            if (qGroup.getLeader().getUUID().equals(leader))
            {
                return qGroup;
            }
        }
        return null;
    }

    public QGroup getGroupByPlayer(UUID player)
    {
        for (QGroup qGroup : queue)
        {
            if (qGroup == null)
                continue;
            if (qGroup.contains(player))
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
            if (qGroup == null)
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
        if (group == null)
            return false;
        return group.removeQPlayer(uuid);
    }

    public boolean containsUUID(UUID uuid)
    {
        for (QGroup qGroup : queue)
        {
            if (qGroup == null)
                continue;

            if (qGroup.contains(uuid))
            {
                return true;
            }
        }
        return false;
    }

    public boolean containsLeader(UUID uuid)
    {
        for (QGroup qGroup : queue)
        {
            if (qGroup == null)
                continue;
            if (qGroup.getLeader().getUUID().equals(uuid))
            {
                return true;
            }
        }
        return false;
    }

    public int getSize()
    {
        int i = 0;
        for (QGroup group : queue)
        {
            i += group.getSize();
        }
        return i;
    }

    public void updateInfosToHub()
    {
        sendInfo = true;
    }

    public void sendInfoToHub()
    {
        try
        {
            if (System.currentTimeMillis() - lastSend > 60000 || sendInfo)
            {
                GameInfosToHubPacket packet = new GameInfosToHubPacket(template.getId());
                packet.setPlayerMaxForMap(template.getMaxSlot());
                packet.setPlayerWaitFor(getSize());
                List<MinecraftServerS> serverSList = instance.getClientManager().getServersByTemplate(template);
                int nb = 0;
                for (MinecraftServerS serverS : serverSList)
                {
                    nb += serverS.getActualSlots();
                }
                packet.setTotalPlayerOnServers(nb);

                manager.sendPacketHub(packet);
                lastSend = System.currentTimeMillis();
                sendInfo = false;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getName()
    {
        return template.getId();
    }

    public String getGame()
    {
        return game;
    }

    public String getMap()
    {
        return map;
    }

    public AbstractGameTemplate getTemplate()
    {
        return template;
    }
}
