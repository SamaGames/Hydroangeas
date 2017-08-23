package net.samagames.hydroangeas.server;

import joptsimple.OptionSet;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.server.algo.AlgorithmicMachine;
import net.samagames.hydroangeas.server.algo.HostGameManager;
import net.samagames.hydroangeas.server.algo.TemplateManager;
import net.samagames.hydroangeas.server.client.ClientManager;
import net.samagames.hydroangeas.server.commands.ServerCommandManager;
import net.samagames.hydroangeas.server.connection.ServerConnectionManager;
import net.samagames.hydroangeas.server.games.AbstractGameTemplate;
import net.samagames.hydroangeas.server.hubs.HubBalancer;
import net.samagames.hydroangeas.server.receiver.ServerStatusReceiver;
import net.samagames.hydroangeas.server.receiver.ServiceRequestReceiver;
import net.samagames.hydroangeas.server.waitingqueue.Queue;
import net.samagames.hydroangeas.server.waitingqueue.QueueManager;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

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
public class HydroangeasServer extends Hydroangeas
{
    public ServerConnectionManager connectionManager;
    private ClientManager clientManager;
    private AlgorithmicMachine algorithmicMachine;
    private HostGameManager hostGameManager;

    private TemplateManager templateManager;

    private QueueManager queueManager;

    private HubBalancer hubBalancer;

    private Timer resetTimer;

    public HydroangeasServer(OptionSet options) throws IOException
    {
        super(options);
    }

    @Override
    public void enable()
    {
        this.log(Level.INFO, "Starting Hydroangeas server...");

        this.connectionManager = new ServerConnectionManager(this);

        this.redisSubscriber.registerReceiver("global@hydroangeas-server", data -> {
            try
            {
                connectionManager.getPacket(data);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });
        this.redisSubscriber.registerReceiver("serverUpdateChannel", new ServerStatusReceiver(this));
        this.redisSubscriber.registerReceiver("hubUpdateChannel", new ServerStatusReceiver(this));
        this.redisSubscriber.registerReceiver("hubsChannel", new ServerStatusReceiver(this));

        this.redisSubscriber.registerReceiver("samaconnect.services.requests", new ServiceRequestReceiver(this));

        this.clientManager = new ClientManager(this);

        this.algorithmicMachine = new AlgorithmicMachine(this);
        this.hostGameManager = new HostGameManager(this);

        this.queueManager = new QueueManager(this);

        this.templateManager = new TemplateManager(this);

        this.commandManager = new ServerCommandManager(this);

        ModMessage.sendMessage(InstanceType.SERVER, "Démarrage d'Hydroangeas Server...");
        ModMessage.sendMessage(InstanceType.SERVER, "> Récupération des données éxistantes (20 secondes)...");

        this.hubBalancer = new HubBalancer(this);

        clientManager.globalCheckData();

        resetTimer = new Timer(true);
        ZonedDateTime dateTime = ZonedDateTime.now();
        resetTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                resetStats();
            }
        }, new Date(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth()), TimeUnit.HOURS.toMillis(24));
    }

    @Override
    public void disable()
    {
        queueManager.disable();
        resetTimer.cancel();
        ModMessage.sendMessage(InstanceType.SERVER, "Arrêt demandé ! Attention, le network ne sera plus géré !");
    }

    public void resetStats()
    {
        getLogger().info("ATTENTION");
        getLogger().info("Suppression des stats du jour.");
        for(AbstractGameTemplate template : getTemplateManager().getTemplates())
        {
            try{
                template.resetStats();
            }catch (Exception e)
            {
                getLogger().severe("Cannot reset stats for template: " + template.getId());
            }
        }

        for(Queue queue : getQueueManager().getQueues())
        {
            try{
                queue.resetStats();
            }catch (Exception e)
            {
                getLogger().severe("Cannot reset stats for queue: " + queue.getName());
            }
        }

    }

    public UUID getServerUUID()
    {
        return getUUID();
    }

    public ServerConnectionManager getConnectionManager()
    {
        return connectionManager;
    }

    public ClientManager getClientManager()
    {
        return this.clientManager;
    }

    public AlgorithmicMachine getAlgorithmicMachine()
    {
        return this.algorithmicMachine;
    }

    public QueueManager getQueueManager()
    {
        return queueManager;
    }

    public TemplateManager getTemplateManager()
    {
        return templateManager;
    }

    public HubBalancer getHubBalancer()
    {
        return hubBalancer;
    }

    public HostGameManager getHostGameManager() {
        return hostGameManager;
    }
}
