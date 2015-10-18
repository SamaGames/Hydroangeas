package net.samagames.hydroangeas.server.hubs;

import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.MinecraftServerS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Silva on 13/09/2015.
 */
public class BalancingTask extends Thread
{
    public static final int HUB_SAFETY_MARGIN = 2;
    public static final int HUB_CONSIDERED_AS_EMPTY = 5; //Number minimum of player on a HUB we can stop
    private HubBalancer hubBalancer;
    private int coolDown = 0; //*100ms

    public BalancingTask(HydroangeasServer instance, HubBalancer hubBalancer)
    {
        this.hubBalancer = hubBalancer;
        coolDown = 350; //Wait 20s to load balance hub
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                checkCooldown();

                int requestNumber = needNumberOfHub();

                if (hubBalancer.getNumberServer() < requestNumber)
                {
                    for (int i = requestNumber - hubBalancer.getNumberServer(); i >= 0; i--)
                    {
                        hubBalancer.startNewHub();
                    }
                    coolDown += 15;
                } else if (hubBalancer.getNumberServer() > requestNumber)
                {
                    List<MinecraftServerS> balancedHubList = new ArrayList<>();
                    balancedHubList.addAll(hubBalancer.getBalancedHubList());
                    for (MinecraftServerS serverS : balancedHubList)
                    {
                        if (hubBalancer.getNumberServer() == requestNumber)
                            break;

                        if (serverS.getActualSlots() < HUB_CONSIDERED_AS_EMPTY)
                        {
                            serverS.dispatchCommand("evacuate lobby");
                            hubBalancer.onHubShutdown(serverS);
                            hubBalancer.getInstance().getScheduler().schedule(() -> serverS.shutdown(), 65, TimeUnit.SECONDS);

                        }
                    }
                }
                Thread.sleep(1500);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public int needNumberOfHub()
    {
        return (hubBalancer.getUsedSlots() / hubBalancer.getHubTemplate().getMaxSlot()) + HUB_SAFETY_MARGIN + 1;
    }

    public void checkCooldown() throws InterruptedException
    {
        while (coolDown > 0)
        {
            coolDown--;
            Thread.sleep(100);
        }
        coolDown = 0;//Flemme de calculer
    }

}
