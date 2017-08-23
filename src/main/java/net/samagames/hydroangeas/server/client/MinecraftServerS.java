package net.samagames.hydroangeas.server.client;

import com.google.gson.JsonElement;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.data.MinecraftServer;
import net.samagames.hydroangeas.common.protocol.hubinfo.HostGameInfoToHubPacket;
import net.samagames.hydroangeas.common.protocol.intranet.AskForClientActionPacket;
import net.samagames.hydroangeas.common.protocol.intranet.MinecraftServerSyncPacket;
import net.samagames.hydroangeas.common.samapi.GameConnector;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.data.Status;
import net.samagames.hydroangeas.server.games.AbstractGameTemplate;
import net.samagames.hydroangeas.server.tasks.CleanServer;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 * This file is part of Hydroangeas.
 *
 * Hydroangeas is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hydroangeas is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hydroangeas.  If not, see <http://www.gnu.org/licenses/>.
 */
public class MinecraftServerS extends MinecraftServer
{
    private HydroClient client;

    private boolean started;

    private boolean available;

    private List<Runnable> onStartHook;

    private int suppressionFlag = 0;

    public MinecraftServerS(HydroClient client, AbstractGameTemplate template)
    {
        this(client,
                UUID.randomUUID(),
                template.getGameName(),
                template.getMapName(),
                template.getMinSlot(),
                template.getMaxSlot(),
                template.getOptions(),
                template.getStartupOptions());

        this.coupaingServer = template.isCoupaing();
        this.templateID = template.getId();
        this.weight = template.getWeight();
    }

    public MinecraftServerS(HydroClient client, MinecraftServerSyncPacket packet)
    {
        this(client,
                packet.getMinecraftUUID(),
                packet.getGame(),
                packet.getMap(),
                packet.getMinSlot(),
                packet.getMaxSlot(),
                packet.getOptions(),
                packet.getStartupOptions());

        this.templateID = packet.getTemplateID();
        this.port = packet.getPort();
        this.hubID = packet.getHubID();
        this.weight = packet.getWeight();
    }

    public MinecraftServerS(HydroClient client,
                            UUID uuid,
                            String game,
                            String map,
                            int minSlot,
                            int maxSlot,
                            JsonElement options,
                            JsonElement startupOptions)
    {
        super(uuid,
                game,
                map,
                minSlot,
                maxSlot,
                options,
                startupOptions);

        this.client = client;

        onStartHook = new ArrayList<>();
    }

    public void shutdown()
    {
        client.getInstance().getConnectionManager().sendPacket(client,
                new AskForClientActionPacket(client.getUUID(), AskForClientActionPacket.ActionCommand.SERVEREND, getServerName()));
    }

    public void onStarted()
    {
        List<Runnable> hooks = new ArrayList<>();
        hooks.addAll(onStartHook);
        onStartHook.clear();
        for(Runnable runnable : hooks)
        {
            client.getInstance().getScheduler().execute(runnable);
        }

        updateHubHostGame(0);

       /* String ip = this.client.getIp();
        int port = getPort();
        //Register server in redis cache
        Jedis jedis = Hydroangeas.getInstance().getDatabaseConnector().getResource();
        jedis.hset("servers", getServerName(), ip + ":" + port);
        jedis.close();*/

        //Register server to all bungee
        //Hydroangeas.getInstance().getRedisSubscriber().send("servers", "heartbeat " + getServerName() + " " + ip + " " + port);
    }

    public void onShutdown()
    {
        if(isCoupaingServer())
        {
            Hydroangeas.getInstance().getAsServer().getHostGameManager().removeServer(getServerName());
        }

        //If we need to save some data after shutdown
        if (isHub())
        {
            Hydroangeas.getInstance().getAsServer().getHubBalancer().onHubShutdown(this);
        }
        unregisterNetwork();
        updateHubHostGame(2);
    }

    public void addOnStartHook(Runnable runnable)
    {
        onStartHook.add(runnable);
    }

    public void unregisterNetwork()
    {
        //Security remove server from redis
        Jedis jedis = Hydroangeas.getInstance().getDatabaseConnector().getResource();
        jedis.hdel("servers", getServerName());
        jedis.close();

        //Send to all bungee the server shutdown event
        Hydroangeas.getInstance().getRedisSubscriber().send("servers", "stop " + getServerName());
    }

    public void dispatchCommand(String command)
    {
        Hydroangeas.getInstance().getRedisSubscriber().send("commands.servers."+getServerName(), command);
    }

    public boolean isStarted()
    {
        return started;
    }

    public void setStarted(boolean started)
    {
        this.started = started;
    }

    public void setStatus(Status status)
    {
        if(this.status.equals(Status.STARTING) && status.equals(Status.WAITING_FOR_PLAYERS))
        {
            try{
                AbstractGameTemplate templateByID = HydroangeasServer.getInstance().getAsServer().getTemplateManager().getTemplateByID(this.templateID);
                if(!isCoupaingServer())
                    templateByID.addTimeToStart(System.currentTimeMillis() - startedTime);
            }catch (Exception e)
            {
                Hydroangeas.getLogger().severe("Error to save starting stat for: " + getServerName());
                Hydroangeas.getLogger().severe("Prevent starting system may fail");
            }
        }
        this.status = status;
        updateHubHostGame(1);
    }

    public void setActualSlots(int actualSlots)
    {
        if(!available && actualSlots >= 1)
        {
            available = true;
            timeToLive = CleanServer.LIVETIME;
        }
        this.actualSlots = actualSlots;
        updateHubHostGame(1);
    }

    public void updateHubHostGame(int state)
    {
        if(!coupaingServer)
            return;

        HostGameInfoToHubPacket packet = new HostGameInfoToHubPacket();
        packet.setCreator(owner);
        packet.setEvent(uuid);
        packet.setPlayerMaxForMap(maxSlot);
        packet.setPlayerWaitFor(minSlot);
        packet.setTotalPlayerOnServers(actualSlots);
        packet.setState(state);
        packet.setServerName(this.getServerName());
        packet.setTemplateId(this.getTemplateID());

        Hydroangeas.getInstance().getAsServer().getConnectionManager().sendPacket("hydroHubReceiver", packet);
    }


    public HydroClient getClient() {
        return client;
    }

    public void sendPlayer(UUID uuid)
    {
        GameConnector.sendPlayerToServer(getServerName(), uuid);
        //TODO check if player is connected
        /*client.getInstance().getScheduler().schedule(new Runnable() {
            @Override
            public void run() {

            }
        })*/
    }
}
