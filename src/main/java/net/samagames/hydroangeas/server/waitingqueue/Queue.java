package net.samagames.hydroangeas.server.waitingqueue;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.packets.AbstractPacket;
import net.samagames.hydroangeas.common.protocol.hubinfo.GameInfosToHubPacket;
import net.samagames.hydroangeas.common.protocol.queues.QueueInfosUpdatePacket;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.server.games.AbstractGameTemplate;
import net.samagames.hydroangeas.server.games.PackageGameTemplate;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static net.samagames.hydroangeas.Hydroangeas.getLogger;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Silvanosky on 12/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class Queue
{

    private ScheduledFuture hubRefreshTask;
    private ScheduledFuture queueChecker;

    private QueueManager manager;

    private HydroangeasServer instance;

    private PriorityPlayerQueue queue;

    private AbstractGameTemplate template;

    //Anticipation

    private DataQueue dataQueue;

    // Manager

    private WatchQueue watchQueue;

    //Hub data

    private volatile boolean sendInfo = true;

    private long lastSend = System.currentTimeMillis();

    //Stats
    private long lastJoinTime = System.currentTimeMillis();


    public Queue(QueueManager manager, AbstractGameTemplate template)
    {
        this.instance = Hydroangeas.getInstance().getAsServer();
        this.manager = manager;
        this.template = template;
        this.dataQueue = new DataQueue(instance);

        if (template instanceof PackageGameTemplate)//Assuming that the package template have not selected a template we force it
        {
            ((PackageGameTemplate) template).selectTemplate();
        }

        //Si priority plus grande alors tu passe devant.
        this.queue = new PriorityPlayerQueue(100000, Comparator.comparingInt(QGroup::getPriority));

        watchQueue = new WatchQueue(instance, this);

        queueChecker = instance.getScheduler().scheduleAtFixedRate(() -> {
            //Control check
            try{
                if(!watchQueue.isProcessing())
                {
                    getLogger().info("Queue worker stopped ! For: " + template.getId());
                    getLogger().info("Restarting task..");
                    watchQueue.startProcess();
                }

                //Player inform
                List<QGroup> groups = new ArrayList<>();
                groups.addAll(queue);
                int queueSize = getSize();
                int index = 0;
                for(int i = 0; i < groups.size(); i++)
                {
                    QGroup group = groups.get(i);
                    index += group.getSize();

                    for(QPlayer player : group.getQPlayers())
                    {
                        sendInfoToPlayer(group, player, index, i+1, queueSize);
                    }
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }, 0, 10, TimeUnit.SECONDS);

        hubRefreshTask = instance.getScheduler().scheduleAtFixedRate(this::sendInfoToHub, 0, 750, TimeUnit.MILLISECONDS);
    }

    public long sendGroups(List<MinecraftServerS> servers)
    {
        long n = 0;
        for(MinecraftServerS s : servers) {
            List<QGroup> groups = new ArrayList<>();
            queue.drainPlayerTo(groups, s.getMaxSlot() - s.getActualSlots());
            for (QGroup group : groups) {
                group.sendTo(s);
            }

            n += 1;
        }
        return n;
    }

    public void remove()
    {
        watchQueue.stop();
        hubRefreshTask.cancel(true);
        queueChecker.cancel(true);
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
            if (qGroup == null || qGroup.getLeader() == null)
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
        List<QGroup> cache = new ArrayList<>();
        cache.addAll(queue);
        for(QGroup group : cache)
        {
            i += group.getSize();
        }
        return i;
    }

    public void updateInfosToHub()
    {
        sendInfo = true;
    }

    private void sendInfoToPlayer(QGroup group, QPlayer player, int index, int place, int queueSize)
    {
        QueueInfosUpdatePacket queueInfosUpdatePacket = new QueueInfosUpdatePacket(player, QueueInfosUpdatePacket.Type.INFO, getGame(), getMap());
        int nbAvailable = template.getMaxSlot() * dataQueue.getLastServerStartNB();
        queueInfosUpdatePacket.setStarting(false);
        if(index < nbAvailable)
        {
            queueInfosUpdatePacket.setStarting(true);
            queueInfosUpdatePacket.setTimeToStart(template.getTimeToStart());
        }else{
            int n = template.getMinSlot() - (queueSize - nbAvailable);
            queueInfosUpdatePacket.setRemainingPlayer(n);
        }

        queueInfosUpdatePacket.setPlace(place);
        if(group.getSize() > 1)
        {
            queueInfosUpdatePacket.setGroupSize(group.getSize());
        }
        sendPacketHub(queueInfosUpdatePacket);
    }

    private void sendInfoToHub()
    {
        try
        {
            if(template == null)
                return;

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

    private void sendPacketHub(AbstractPacket packet)
    {
        instance.getConnectionManager().sendPacket("hydroHubReceiver", packet);
    }

    public void reload(AbstractGameTemplate template)
    {
        synchronized (this.template)
        {
            this.template = template;
        }
    }

    public String getName()
    {
        return template.getId();
    }

    public String getGame()
    {
        return template.getGameName();
    }

    public String getMap()
    {
        return template.getMapName();
    }

    public AbstractGameTemplate getTemplate()
    {
        return template;
    }

    public DataQueue getDataQueue() {
        return dataQueue;
    }

    public WatchQueue getWatchQueue() {
        return watchQueue;
    }

    public void resetStats()
    {
        //Todo reset stats
    }


}
