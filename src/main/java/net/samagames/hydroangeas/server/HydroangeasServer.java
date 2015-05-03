package net.samagames.hydroangeas.server;

import joptsimple.OptionSet;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.server.http.HttpServerManager;
import net.samagames.hydroangeas.server.http.NetworkHttpHandler;
import net.samagames.hydroangeas.server.packets.ServerReceiver;

import java.util.logging.Level;

public class HydroangeasServer extends Hydroangeas
{
    private HttpServerManager httpServerManager;

    public HydroangeasServer(OptionSet options)
    {
        super(options);
    }

    @Override
    public void enable()
    {
        this.log(Level.INFO, "Starting Hydroangeas server...");

        this.redisSubscriber.registerReceiver("hydroangeas-server", new ServerReceiver());

        this.httpServerManager = new HttpServerManager(this);
        this.httpServerManager.getHttpServer().createContext("/network", new NetworkHttpHandler());
    }

    @Override
    public void shutdown()
    {
        super.shutdown();
        this.httpServerManager.disable();
    }
}
