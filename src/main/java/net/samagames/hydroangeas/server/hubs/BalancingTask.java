package net.samagames.hydroangeas.server.hubs;

import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.MinecraftServerS;

/**
 * Created by Silva on 13/09/2015.
 */
public class BalancingTask extends Thread {

    private HydroangeasServer instance;
    private HubBalancer hubBalancer;

    public static final int HUB_SAFETY_MARGIN = 2;
    public static final int HUB_CONSIDERED_AS_EMPTY = 5; //Number minimum of player on a HUB we can stop

    private int coolDown = 0; //*100ms

    public BalancingTask(HydroangeasServer instance, HubBalancer hubBalancer)
    {
        this.instance = instance;
        this.hubBalancer = hubBalancer;
    }

    @Override
    public void run() {
        while(true)
        {
            try {
                int requestNumber = needNumberOfHub();

                if(hubBalancer.getNumberServer() < requestNumber)
                {
                    for(int i = requestNumber - hubBalancer.getNumberServer(); i > 0; i--)
                    {
                        hubBalancer.startNewHub();
                    }
                    coolDown += 15;
                }else if(hubBalancer.getNumberServer() > requestNumber)
                {
                    for(MinecraftServerS serverS : hubBalancer.getBalancedHubList())
                    {
                        if(hubBalancer.getNumberServer() == requestNumber)
                            break;

                        if(serverS.getActualSlots() < HUB_CONSIDERED_AS_EMPTY)
                        {
                            serverS.shutdown();
                        }
                    }
                }

                checkCooldown();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int needNumberOfHub()
    {
        return (hubBalancer.getUsedSlots() / hubBalancer.getHubTemplate().getMaxSlot()) + HUB_SAFETY_MARGIN;
    }

    public void checkCooldown() throws InterruptedException {
        while (coolDown > 0)
        {
            coolDown--;
            Thread.sleep(100);
        }
        coolDown = 0;//Flemme de calculer
    }

}
