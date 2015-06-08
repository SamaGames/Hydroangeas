package net.samagames.hydroangeas.client.schedulers;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.servers.MinecraftServer;
import net.samagames.hydroangeas.utils.ModMessage;

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
        ModMessage.sendDebug("loop -> " + this.server.getServerFolder().exists());

        if(!this.server.getServerFolder().exists())
            this.instance.getServerManager().onServerStop(this.server);
    }
}
