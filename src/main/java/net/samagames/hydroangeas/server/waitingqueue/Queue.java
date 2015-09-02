package net.samagames.hydroangeas.server.waitingqueue;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.protocol.hubinfo.GameInfosToHubPacket;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.server.data.Status;
import net.samagames.hydroangeas.server.games.BasicGameTemplate;
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

    private HydroangeasServer instance;

    private String game;
    private String map;

    private PriorityBlockingQueue<QGroup> queue;

    private Thread updater;
    private boolean sendInfo = true;

    private Thread worker;
    private boolean working = true;

    private BasicGameTemplate template;

    /*public Queue(QueueManager manager, String name)
    {
        this(manager, name.split("_")[0], name.split("_")[1]);
    }*/

    public Queue(QueueManager manager, BasicGameTemplate template)
    {
        this.instance = Hydroangeas.getInstance().getAsServer();
        this.manager = manager;
        this.template = template;
        this.game = template.getGameName();
        this.map = template.getMapName();

        //Si priority plus grande alors tu passe devant.
        this.queue = new PriorityBlockingQueue<>(100000, (o1, o2) -> -Integer.compare(o1.getPriority(), o2.getPriority()));

        worker = new Thread(() -> {
            while (working)
            {
                if(template == null)
                {
                    Hydroangeas.getInstance().getLogger().info("Template null!");
                    return;
                }

                List<MinecraftServerS> servers = instance.getAlgorithmicMachine().getServerByTemplatesAndAvailable(template.getId());

                for(MinecraftServerS server : servers)
                {
                    if(server.getStatus().isAllowJoin())
                    {
                        List<QGroup> groups = new ArrayList<>();
                        queue.drainTo(groups, server.getMaxSlot());
                        for(QGroup group : groups)
                        {
                            group.sendTo(server.getServerName());
                        }
                    }else if(!server.getStatus().equals(Status.STARTING))
                    {
                        //ne devrait jamais arrriver (peut etre à delete)
                        servers.remove(server);
                    }
                }

                if(servers.size() <= 0 && queue.size() >= template.getMaxSlot() * 0.7)
                {
                    Hydroangeas.getInstance().getAsServer().getAlgorithmicMachine().orderTemplate(template);
                }

                try {
                    Thread.sleep(900L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, template.getId() + " Worker");
        worker.start();

        updater = new Thread(() -> {
            long lastSend = System.currentTimeMillis();
            while(true)
            {
                try{
                    if(System.currentTimeMillis() - lastSend > 1 * 60 * 1000 || sendInfo)
                    {
                        sendInfoToHub();
                        sendInfo = false;
                    }

                    Thread.sleep(700);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, template.getId() + " Updater");
        updater.start();

    }

    public void remove()
    {
        working = false;
        worker.interrupt();
        updater.interrupt();
    }

    public boolean addPlayersInNewGroup(QPlayer leader, List<QPlayer> players)
    {
        return addGroup(new QGroup(leader, players));
    }

    public boolean addGroup(QGroup qGroup)
    {
        try{
            return queue.add(qGroup);
        }finally{
            sendInfo = true;
        }
    }

    public boolean removeGroup(QGroup qGroup)
    {
        try{
            return queue.remove(qGroup);
        }finally{
            sendInfo = true;
        }
    }

    public boolean removeQPlayer(QPlayer player)
    {
        try {
            QGroup group = getGroupByPlayer(player.getUUID());
            boolean result = group.removeQPlayer(player);
            removeGroup(group);
            if (group.getLeader() != null) {
                addGroup(group);
            }
            return result;
        }finally{
            sendInfo = true;
        }
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

    public void sendInfoToHub()
    {
        instance.getScheduler().execute(() -> {
            GameInfosToHubPacket packet = new GameInfosToHubPacket(template.getId());
            packet.setPlayerMaxForMap(template.getMaxSlot());
            packet.setPlayerWaitFor(getSize());
            List<MinecraftServerS> serverSList = instance.getClientManager().getServersByTemplate(template);
            int nb = 0;
            for(MinecraftServerS serverS : serverSList)
            {
                nb += serverS.getActualSlots();
            }
            packet.setTotalPlayerOnServers(nb);

            manager.sendPacketHub(packet);
        });
    }

    public String getName()
    {
        return template.getId();
    }

    public String getGame() {
        return game;
    }

    public String getMap() {
        return map;
    }

    public BasicGameTemplate getTemplate()
    {
        return Hydroangeas.getInstance().getAsServer().getTemplateManager().getTemplateByGameAndMap(game, map);
    }
}
