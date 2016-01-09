package net.samagames.hydroangeas.server.waitingqueue;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.packets.AbstractPacket;
import net.samagames.hydroangeas.common.protocol.hubinfo.GameInfosToHubPacket;
import net.samagames.hydroangeas.common.protocol.queues.QueueInfosUpdatePacket;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.server.data.Status;
import net.samagames.hydroangeas.server.games.AbstractGameTemplate;
import net.samagames.hydroangeas.server.games.PackageGameTemplate;
import net.samagames.hydroangeas.server.tasks.CleanServer;
import net.samagames.hydroangeas.utils.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 12/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class Queue
{

    private ScheduledFuture workerTask, hubRefreshTask, queueChecker;
    private QueueManager manager;

    private HydroangeasServer instance;

    private PriorityPlayerQueue queue;
    private volatile boolean sendInfo = true;

    private AbstractGameTemplate template;

    private long lastSend = System.currentTimeMillis();

    private int coolDown = 2; //*100ms

    private AtomicInteger lastServerStartNB = new AtomicInteger(0);

    public Queue(QueueManager manager, AbstractGameTemplate template)
    {
        this.instance = Hydroangeas.getInstance().getAsServer();
        this.manager = manager;
        this.template = template;

        if (template instanceof PackageGameTemplate)//Assuming that the package template have not selected a template we force it
        {
            ((PackageGameTemplate) template).selectTemplate();
        }

        //Si priority plus grande alors tu passe devant.
        this.queue = new PriorityPlayerQueue(100000, (o1, o2) -> Integer.compare(o1.getPriority(), o2.getPriority()));

        startQueueWorker();

        queueChecker = instance.getScheduler().scheduleAtFixedRate(() -> {
            //Control check
            try{
                if(workerTask != null)
                {
                    if(workerTask.isDone())
                    {
                        instance.getLogger().info("Queue worker stopped ! For: " + template.getId());
                        instance.getLogger().info("Restarting task..");
                        startQueueWorker();
                    }
                }

                //Player inform
                List<QGroup> groups = new ArrayList<>();
                groups.addAll(queue);
                int queueSize = getSize();
                int index = 0;
                for(int i = 0; i< groups.size(); i++)
                {
                    QGroup group = groups.get(i);
                    index += group.getSize();

                    for(QPlayer player : group.getQPlayers())
                    {
                        List<String> messages = new ArrayList<>();
                        QueueInfosUpdatePacket queueInfosUpdatePacket = new QueueInfosUpdatePacket(player, QueueInfosUpdatePacket.Type.INFO, getGame(), getMap());
                        if(index < template.getMaxSlot()*lastServerStartNB.get())
                        {
                            messages.add(ChatColor.GREEN + "Votre serveur est en train de démarrer !");
                        }else{
                            messages.add(ChatColor.RED + "Votre serveur n'a pas encore démarré.");
                            if(queueSize < template.getMaxSlot())
                            {
                                messages.add("Il manque " + ChatColor.RED + (template.getMinSlot() - queueSize) + "<RESET> joueur(s) pour qu'il démarre.");
                            }
                        }

                        messages.add("Vous êtes actuellement à la place " + ChatColor.RED + (i+1) + "<RESET> dans la file.");
                        if(group.getSize() > 1)
                        {
                            messages.add("Votre groupe est composé de "+ group.getSize() + " personnes.");
                        }

                        queueInfosUpdatePacket.setMessage(messages);
                        sendPacketHub(queueInfosUpdatePacket);
                    }
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }, 0, 15, TimeUnit.SECONDS);
        hubRefreshTask = instance.getScheduler().scheduleAtFixedRate(this::sendInfoToHub, 0, 750, TimeUnit.MILLISECONDS);

    }

    public void startQueueWorker()
    {
        workerTask = instance.getScheduler().scheduleAtFixedRate(() ->
        {
            try{
                if (template == null)
                {
                    Hydroangeas.getInstance().getLogger().info("Template null!");
                    return;
                }

                try {
                    checkCooldown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                List<MinecraftServerS> servers = instance.getAlgorithmicMachine().getServerByTemplatesAndAvailable(template.getId());

                servers.stream().filter(server -> (server.getStatus().equals(Status.WAITING_FOR_PLAYERS) || server.getStatus().equals(Status.READY_TO_START))
                        && server.getMaxSlot() > server.getActualSlots()).forEach(server -> {

                    List<QGroup> groups = new ArrayList<>();
                    queue.drainPlayerTo(groups, server.getMaxSlot() - server.getActualSlots());
                    for (QGroup group : groups) {
                        group.sendTo(server.getServerName());
                    }
                    coolDown += 16;
                });
                lastServerStartNB.lazySet(servers.size());

                if (servers.size() <= 0 && getSize() >= template.getMinSlot())
                {
                    MinecraftServerS server = Hydroangeas.getInstance().getAsServer().getAlgorithmicMachine().orderTemplate(template);
                    if(server != null)
                    {
                        server.setTimeToLive(150000L);
                    }
                    if (template instanceof PackageGameTemplate) // If it's a package template we change it now
                    {
                        ((PackageGameTemplate) template).selectTemplate();
                    }
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }, 0, 800, TimeUnit.MILLISECONDS);
    }

    public void checkCooldown() throws InterruptedException
    {
        while (coolDown > 0)
        {
            coolDown--;
            Thread.sleep(100);
        }
        coolDown = 0;//Security in case of forgot
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

    public void sendInfoToHub()
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

    public void sendPacketHub(AbstractPacket packet)
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

}
