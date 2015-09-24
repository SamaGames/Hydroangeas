package net.samagames.hydroangeas.server.hubs;

import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.server.games.SimpleGameTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Silva on 13/09/2015.
 */
public class HubBalancer {

    private HydroangeasServer instance;

    private BalancingTask balancer;

    private SimpleGameTemplate hubTemplate;

    private List<MinecraftServerS> hubs = new ArrayList<>();

    public HubBalancer(HydroangeasServer instance)
    {
        this.instance = instance;

        balancer = new BalancingTask(instance, this);

        balancer.start();

        try{
            hubTemplate = (SimpleGameTemplate) instance.getTemplateManager().getTemplateByID("Hub");
        }catch (Exception e)
        {
            e.printStackTrace();

            instance.getLogger().severe("No Hub template found !");
            instance.getLogger().severe("Add one and reboot HydroServer or no hub will be start on the network!");
        }
    }

    public int getNumberServer()
    {
        return hubs.size();
    }

    public int getTotalSlot()
    {
        int i = 0;
        for(MinecraftServerS serverS : hubs)
        {
            i+= serverS.getMaxSlot();
        }
        return i;
    }

    public List<MinecraftServerS> getBalancedHubList()
    {
        return hubs;
    }

    public boolean stopBalancing()
    {
        balancer.interrupt();
        return true;
    }

    public SimpleGameTemplate getHubTemplate()
    {
        return hubTemplate;
    }

}
