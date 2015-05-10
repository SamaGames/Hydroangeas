package net.samagames.hydroangeas.server;

import joptsimple.OptionSet;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.server.http.HttpServerManager;
import net.samagames.hydroangeas.server.http.NetworkHttpHandler;
import net.samagames.hydroangeas.server.packets.HelloClientPacketReceiver;

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

        this.httpServerManager = new HttpServerManager(this);
        this.httpServerManager.getHttpServer().createContext("/network", new NetworkHttpHandler());

        this.clientManager = new ClientManager(this);
    }

    @Override
    public void shutdown()
    {
        super.shutdown();
        this.httpServerManager.disable();
    }

    public ClientManager getClientManager()
    {
        return this.clientManager;
    }
}
