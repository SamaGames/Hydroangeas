package net.samagames.hydroangeas.server;

import joptsimple.OptionSet;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.server.packets.CoupaingServerReceiver;
import net.samagames.hydroangeas.server.scheduler.StartThread;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

import java.util.logging.Level;

public class HydroangeasServer extends Hydroangeas
{
    public ServerConnectionManager connectionManager;
    private ClientManager clientManager;
    private AlgorithmicMachine algorithmicMachine;

    public HydroangeasServer(OptionSet options)
    {
        super(options);
    }

    @Override
    public void enable()
    {
        this.log(Level.INFO, "Starting Hydroangeas server...");

        connectionManager = new ServerConnectionManager(this);

        this.redisSubscriber.registerReceiver("global@hydroangeas-server", data -> connectionManager.getPacket(data));
        this.redisSubscriber.registerReceiver("coupaing@hydroangeas-server", new CoupaingServerReceiver());

        this.clientManager = new ClientManager(this);
        this.algorithmicMachine = new AlgorithmicMachine(this);

        new StartThread().start();
    }

    @Override
    public void shutdown()
    {
        ModMessage.sendMessage(InstanceType.SERVER, "Arrêt demandé ! Attention, les serveurs ne seront plus automatiquement balancés !");

        super.shutdown();

        this.clientManager.getKeepUpdatedThread().stop();
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
