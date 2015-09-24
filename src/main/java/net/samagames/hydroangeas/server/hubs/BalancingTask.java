package net.samagames.hydroangeas.server.hubs;

import net.samagames.hydroangeas.server.HydroangeasServer;

/**
 * Created by Silva on 13/09/2015.
 */
public class BalancingTask extends Thread {

    private HydroangeasServer instance;
    private HubBalancer hubBalancer;

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



                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
