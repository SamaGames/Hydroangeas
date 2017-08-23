package net.samagames.hydroangeas.server.waitingqueue;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.server.data.Status;
import net.samagames.hydroangeas.server.games.AbstractGameTemplate;
import net.samagames.hydroangeas.server.games.PackageGameTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
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
public class WatchQueue {

    private HydroangeasServer instance;
    private Queue queue;

    private ScheduledFuture task;

    private boolean autoOrder = true;

    private long coolDown = 200;

    WatchQueue(HydroangeasServer instance, Queue queue)
    {
        this.instance = instance;
        this.queue = queue;

        startProcess();
    }

    void startProcess()
    {
        if(isProcessing())
            return;

        task = instance.getScheduler().scheduleAtFixedRate(this::process, 0, 800, TimeUnit.MILLISECONDS);
    }

    boolean isProcessing()
    {
        return task != null && !task.isDone();
    }

    private void process()
    {
        try{
            AbstractGameTemplate template = queue.getTemplate();

            List<MinecraftServerS> servers = instance.getAlgorithmicMachine().getServerByTemplatesAndAvailable(template.getId());

            List<MinecraftServerS> availableServers = new ArrayList<>();
            for(MinecraftServerS s : servers)
            {
                if((s.getStatus().equals(Status.WAITING_FOR_PLAYERS)
                        || s.getStatus().equals(Status.READY_TO_START))
                        && s.getMaxSlot() - s.getActualSlots() > 0)
                    availableServers.add(s);
            }

            long n = queue.sendGroups(availableServers);
            coolDown += 1000 * n;//wait 1 sec for each server filled

            queue.getDataQueue().setLastServerStartNB(servers.size());

            if(autoOrder) {

                checkCooldown();

                //Check if server are started, if not start one
                boolean hasNotEnoughServer = hasNotEnoughServer(servers);

                //If server capacity is less than needed, start we a new server now ? (if there are enough player or if we override)
                if (hasNotEnoughServer
                        && (queue.getSize() >= template.getMinSlot() || queue.getDataQueue().needToAnticipate())) {
                    startTemplateServer();
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void stop()
    {
        task.cancel(true);
    }

    private void startTemplateServer()
    {
        MinecraftServerS server = Hydroangeas.getInstance().getAsServer().getAlgorithmicMachine().orderTemplate(queue.getTemplate());

        //Server started let's proceed
        if (server != null) {
            //To avoid starting loop we wait the real start
            server.addOnStartHook(queue.getDataQueue()::startedServer);

            //Security stop
            server.setTimeToLive(150000L);
            if (queue.getTemplate() instanceof PackageGameTemplate) // If it's a package template we change it now
            {
                ((PackageGameTemplate) queue.getTemplate()).selectTemplate();
            }
        }
    }

    private boolean hasNotEnoughServer(List<MinecraftServerS> servers)
    {
        boolean notEnServer = servers.isEmpty();
        //If already started check if there are all available(not full), if there are none start new one !
        if(!notEnServer)
        {
            int numberOfAvailableServer = servers.size();
            for(MinecraftServerS serverS : servers)
            {
                if(serverS.getMaxSlot() - serverS.getActualSlots() <= 0)
                {
                    numberOfAvailableServer--;
                }
            }
            notEnServer = numberOfAvailableServer <= 0;
        }
        return notEnServer;
    }

    private void checkCooldown()
    {
        try{
            Thread.sleep(coolDown);
        }catch (Exception e)
        {
            //skip
        }
        coolDown = 0;//Security in case of forgot
    }


    public boolean isAutoOrder() {
        return autoOrder;
    }

    public void setAutoOrder(boolean autoOrder) {
        this.autoOrder = autoOrder;
    }
}
