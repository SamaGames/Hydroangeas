package net.samagames.hydroangeas.server.hubs;

import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.server.games.SimpleGameTemplate;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

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
public class HubBalancer
{

    private HydroangeasServer instance;

    private BalancingTask balancer;

    private SimpleGameTemplate hubTemplate;

    private CopyOnWriteArrayList<MinecraftServerS> hubs = new CopyOnWriteArrayList<>();

    public HubBalancer(HydroangeasServer instance)
    {
        this.instance = instance;

        //instance.getScheduler().schedule(() -> loadStartedHubs(), 18, TimeUnit.SECONDS);

        updateHubTemplate();

        instance.getScheduler().scheduleAtFixedRate(() -> {
            ArrayList<MinecraftServerS> serverInfos = new ArrayList<>();
            serverInfos.addAll(hubs);
            for (MinecraftServerS server : serverInfos)
            {
                try
                {
                    Jedis jedis = instance.getDatabaseConnector().getResource();
                    jedis.zrem("lobbybalancer", server.getServerName());
                    jedis.zadd("lobbybalancer", server.getActualSlots(), server.getServerName());
                    jedis.close();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    public boolean updateHubTemplate()
    {
        try
        {
            hubTemplate = (SimpleGameTemplate) instance.getTemplateManager().getTemplateByID("hub");
            if (hubTemplate == null)
                throw new IOException("No Hub template found !");
        } catch (IOException e)
        {
            e.printStackTrace();
            instance.getLogger().severe("Add one and reboot HydroServer or no hub will be start on the network!");
            return false;
        }

        if (balancer == null)
        {
            balancer = new BalancingTask(instance, this);
        }

        if (!balancer.isAlive())
        {
            balancer.start();
        }
        return true;
    }

    public void addStartedHub(MinecraftServerS server)
    {
        if(hubTemplate != null)
        {
            if(hubTemplate.getId().equalsIgnoreCase(server.getTemplateID()))
            {
                hubs.add(server);
                instance.getLogger().info("[HubBalancer] Add already started hub: " + server.getServerName());
            }
        }
    }

    public void loadStartedHubs()
    {
        if(hubTemplate != null)
        {
            for(MinecraftServerS server : instance.getClientManager().getServersByTemplate(hubTemplate))
            {
                hubs.add(server);
                instance.getLogger().info("[HubBalancer] Add already started hub: " + server.getServerName());
            }
        }
    }

    public void startNewHub()
    {
        MinecraftServerS ordered = instance.getAlgorithmicMachine().orderTemplate(hubTemplate);
        if (ordered != null)
            hubs.add(ordered);
    }

    public int getNumberServer()
    {
        return hubs.size();
    }

    public int getUsedSlots()
    {
        int i = 0;
        for (MinecraftServerS serverS : hubs)
        {
            i += serverS.getActualSlots();
        }
        return i;
    }

    public int getTotalSlot()
    {
        int i = 0;
        for (MinecraftServerS serverS : hubs)
        {
            i += serverS.getMaxSlot();
        }
        return i;
    }

    public List<MinecraftServerS> getBalancedHubList()
    {
        return hubs;
    }

    public void stopBalancing()
    {
        if (balancer != null) balancer.interrupt();
    }

    public void onHubShutdown(MinecraftServerS serverS)
    {
        if(serverS == null)
            return;

        try
        {
            Jedis jedis = instance.getDatabaseConnector().getResource();
            jedis.zrem("lobbybalancer", serverS.getServerName());
            jedis.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        hubs.remove(serverS);
        serverS.unregisterNetwork();
    }

    public SimpleGameTemplate getHubTemplate()
    {
        return hubTemplate;
    }

    public HydroangeasServer getInstance()
    {
        return instance;
    }

}
