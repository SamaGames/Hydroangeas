package net.samagames.hydroangeas.server;

import joptsimple.OptionSet;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.server.http.HttpServerManager;
import net.samagames.hydroangeas.server.http.NetworkHttpHandler;
import net.samagames.hydroangeas.server.packets.HelloClientPacketReceiver;
import net.samagames.hydroangeas.server.packets.MinecraftServerIssueReceiver;
import net.samagames.hydroangeas.utils.ChatColor;
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

        this.clientManager = new ClientManager(this);

        ModMessage.sendModMessage("Hydroangeas Server", ChatColor.GREEN, "Prêt !");
    }

    @Override
    public void shutdown()
    {
        ModMessage.sendModMessage("Hydroangeas Server", ChatColor.GREEN, "Arrêt demandé ! Attention, les serveurs ne seront plus automatiquement balancés !");

        super.shutdown();

        this.clientManager.getKeepUpdatedThread().stop();
        this.httpServerManager.disable();
    }

    public ClientManager getClientManager()
    {
        return this.clientManager;
    }
}
