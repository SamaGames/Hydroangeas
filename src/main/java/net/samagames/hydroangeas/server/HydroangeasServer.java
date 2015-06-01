package net.samagames.hydroangeas.server;

import joptsimple.OptionSet;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.server.http.GraphHttpHandler;
import net.samagames.hydroangeas.server.http.HttpServerManager;
import net.samagames.hydroangeas.server.http.NetworkHttpHandler;
import net.samagames.hydroangeas.server.packets.HelloClientPacketReceiver;
import net.samagames.hydroangeas.server.packets.MinecraftServerIssueReceiver;
import net.samagames.hydroangeas.server.scheduler.StartThread;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

import java.util.logging.Level;

public class HydroangeasServer extends Hydroangeas
{
    private HttpServerManager httpServerManager;
    private ClientManager clientManager;

    public HydroangeasServer(OptionSet options)
    {
        super(options);
    }

    @Override
    public void enable()
    {
        this.log(Level.INFO, "Starting Hydroangeas server...");

        this.redisSubscriber.registerReceiver("hello@hydroangeas-server", new HelloClientPacketReceiver());
        this.redisSubscriber.registerReceiver("issue@hydroangeas-server", new MinecraftServerIssueReceiver());

        this.httpServerManager = new HttpServerManager(this);
        this.httpServerManager.getHttpServer().createContext("/network", new NetworkHttpHandler());
        this.httpServerManager.getHttpServer().createContext("/graph", new GraphHttpHandler());

        this.clientManager = new ClientManager(this);

        new StartThread().start();
    }

    @Override
    public void shutdown()
    {
        ModMessage.sendMessage(InstanceType.SERVER, "Arrêt demandé ! Attention, les serveurs ne seront plus automatiquement balancés !");

        super.shutdown();

        this.clientManager.getKeepUpdatedThread().stop();
        this.httpServerManager.disable();
    }

    public ClientManager getClientManager()
    {
        return this.clientManager;
    }
}
