package net.samagames.hydroangeas.server;

import joptsimple.OptionSet;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.protocol.intranet.AskForClientDataPacket;
import net.samagames.hydroangeas.server.algo.AlgorithmicMachine;
import net.samagames.hydroangeas.server.algo.TemplateManager;
import net.samagames.hydroangeas.server.client.ClientManager;
import net.samagames.hydroangeas.server.commands.ServerCommandManager;
import net.samagames.hydroangeas.server.connection.ServerConnectionManager;
import net.samagames.hydroangeas.server.hubs.HubBalancer;
import net.samagames.hydroangeas.server.receiver.ServerStatusReceiver;
import net.samagames.hydroangeas.server.waitingqueue.QueueManager;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class HydroangeasServer extends Hydroangeas
{
    public ServerConnectionManager connectionManager;
    private ClientManager clientManager;
    private AlgorithmicMachine algorithmicMachine;

    private TemplateManager templateManager;

    private QueueManager queueManager;

    private HubBalancer hubBalancer;

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
        this.redisSubscriber.registerReceiver("hubsChannel", new ServerStatusReceiver(this));

        this.clientManager = new ClientManager(this);

        this.algorithmicMachine = new AlgorithmicMachine(this);

        this.queueManager = new QueueManager(this);

        this.templateManager = new TemplateManager(this);

        this.commandManager = new ServerCommandManager(this);

        ModMessage.sendMessage(InstanceType.SERVER, "Démarrage d'Hydroangeas Server...");
        ModMessage.sendMessage(InstanceType.SERVER, "> Récupération des données éxistantes (20 secondes)...");

        this.hubBalancer = new HubBalancer(this);

        connectionManager.sendPacket("globalSecurity@hydroangeas-client", new AskForClientDataPacket(null));
    }

    @Override
    public void disable()
    {
        queueManager.disable();
        ModMessage.sendMessage(InstanceType.SERVER, "Arrêt demandé ! Attention, le network ne sera plus géré !");
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
}
