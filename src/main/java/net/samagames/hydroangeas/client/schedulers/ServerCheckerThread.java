package net.samagames.hydroangeas.client.schedulers;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.servers.MinecraftServer;

public class ServerCheckerThread implements Runnable
{
    private final HydroangeasClient instance;
    private final MinecraftServer server;

    public ServerCheckerThread(HydroangeasClient instance, MinecraftServer server)
    {
        this.instance = instance;
        this.server = server;
    }

    @Override
    public void run()
    {
        if(!this.server.getServerFolder().exists())
            this.instance.getServerManager().onServerStop(this.server);
    }
}
