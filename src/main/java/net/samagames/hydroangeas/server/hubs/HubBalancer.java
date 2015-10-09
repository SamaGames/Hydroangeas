package net.samagames.hydroangeas.server.hubs;

import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.server.games.SimpleGameTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Silva on 13/09/2015.
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
        hubs.stream().filter(server -> server.getServerName().equals(serverS.getServerName())).forEach(server -> hubs.remove(serverS));
    }

    public SimpleGameTemplate getHubTemplate()
    {
        return hubTemplate;
    }

}
