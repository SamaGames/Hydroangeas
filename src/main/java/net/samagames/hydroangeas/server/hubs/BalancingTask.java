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
    public static final double HUB_SAFETY_MARGIN = 2;
    public static final int HUB_CONSIDERED_AS_EMPTY = 5; //Number minimum of player on a HUB we can stop
    private HubBalancer hubBalancer;
    private int coolDown = 0; //*100ms

    public BalancingTask(HydroangeasServer instance, HubBalancer hubBalancer)
    {
        this.hubBalancer = hubBalancer;
        coolDown = 400; //Wait 20s to load balance hub
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                //Wait desired time in case of server start
                checkCooldown();

                //Calculate the needed lobby
                int requestNumber = (int) Math.ceil(needNumberOfHub());

                //Need we some lobby ?
                if (hubBalancer.getNumberServer() < requestNumber)
                {
                    //Start them !
                    for (int i = requestNumber - hubBalancer.getNumberServer(); i > 0; i--)
                    {
                        hubBalancer.startNewHub();
                    }
                    //Wait until started
                    coolDown += 20;

                    //Are they too much lobby ?
                } else if (hubBalancer.getNumberServer() > requestNumber)
                {
                    //Stop them !
                    List<MinecraftServerS> balancedHubList = new ArrayList<>();
                    balancedHubList.addAll(hubBalancer.getBalancedHubList());
                    for (MinecraftServerS serverS : balancedHubList)
                    {
                        if (hubBalancer.getNumberServer() == requestNumber)
                            break;

                        if (serverS.getActualSlots() < HUB_CONSIDERED_AS_EMPTY)
                        {
                            //We are good so we let to players the time to leave the lobby
                            serverS.dispatchCommand("evacuate lobby");
                            hubBalancer.onHubShutdown(serverS);
                            //Security force shutdown
                            hubBalancer.getInstance().getScheduler().schedule(() -> serverS.shutdown(), 65, TimeUnit.SECONDS);
                        }
                    }
                }
                Thread.sleep(300);//Need to be very reactive
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public double needNumberOfHub()
    {
        return ( (((double) hubBalancer.getUsedSlots())*2) / (double) hubBalancer.getHubTemplate().getMaxSlot()) + HUB_SAFETY_MARGIN;
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

}
