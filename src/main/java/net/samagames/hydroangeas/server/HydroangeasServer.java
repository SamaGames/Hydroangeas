package net.samagames.hydroangeas.server;

import joptsimple.OptionSet;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.server.algo.AlgorithmicMachine;
import net.samagames.hydroangeas.server.client.ClientManager;
import net.samagames.hydroangeas.server.commands.ServerCommandManager;
import net.samagames.hydroangeas.server.connection.ServerConnectionManager;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class HydroangeasServer extends Hydroangeas
{
    public ServerConnectionManager connectionManager;
    private ClientManager clientManager;
    private AlgorithmicMachine algorithmicMachine;

    public HydroangeasServer(OptionSet options) throws IOException {
        super(options);
    }

    @Override
    public void enable()
    {
        this.log(Level.INFO, "Starting Hydroangeas server...");

        this.connectionManager = new ServerConnectionManager(this);

        this.redisSubscriber.registerReceiver("global@hydroangeas-server", data -> connectionManager.getPacket(data));

        this.clientManager = new ClientManager(this);
        this.algorithmicMachine = new AlgorithmicMachine(this);

        this.commandManager = new ServerCommandManager(this);

        ModMessage.sendMessage(InstanceType.SERVER, "Démarrage d'Hydroangeas Server...");
        ModMessage.sendMessage(InstanceType.SERVER, "> Récupération des données éxistantes (60 secondes)...");

        this.scheduler.schedule(() -> algorithmicMachine.startMachinery(), 60, TimeUnit.SECONDS);
    }

    public void disable()
    {
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
}
