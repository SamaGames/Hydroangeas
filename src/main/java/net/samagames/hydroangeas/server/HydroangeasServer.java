package net.samagames.hydroangeas.server;

import joptsimple.OptionSet;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.packets.PacketReceiver;
import net.samagames.hydroangeas.server.packets.CoupaingServerReceiver;
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

    private UUID serverUUID;

    public HydroangeasServer(OptionSet options) throws IOException {
        super(options);
    }

    @Override
    public void enable()
    {
        this.log(Level.INFO, "Starting Hydroangeas server...");

        this.serverUUID = UUID.randomUUID();

        this.connectionManager = new ServerConnectionManager(this);

        this.redisSubscriber.registerReceiver("global@hydroangeas-server", new PacketReceiver() {
            @Override
            public void receive(String data) {
                log(Level.INFO, data);
                connectionManager.getPacket(data);
            }
        });
        this.redisSubscriber.registerReceiver("coupaing@hydroangeas-server", new CoupaingServerReceiver());

        this.clientManager = new ClientManager(this);
        this.algorithmicMachine = new AlgorithmicMachine(this);


        ModMessage.sendMessage(InstanceType.SERVER, "Démarrage d'Hydroangeas Server...");
        ModMessage.sendMessage(InstanceType.SERVER, "> Récupération des données éxistantes (60 secondes)...");

        this.scheduler.schedule(() -> Hydroangeas.getInstance().getAsServer().getAlgorithmicMachine().startMachinery(), 60, TimeUnit.SECONDS);
    }

    public void disable()
    {
        ModMessage.sendMessage(InstanceType.SERVER, "Arrêt demandé ! Attention, les serveurs ne seront plus automatiquement balancés !");
    }

    public UUID getServerUUID()
    {
        return serverUUID;
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
