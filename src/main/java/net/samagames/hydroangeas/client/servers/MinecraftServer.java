package net.samagames.hydroangeas.client.servers;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.common.informations.MinecraftServerInfos;

import java.io.File;
import java.util.logging.Level;

public class MinecraftServer
{
    private final HydroangeasClient instance;
    private final MinecraftServerInfos serverInfos;
    private final File serverFolder;
    private int port;

    public MinecraftServer(HydroangeasClient instance, MinecraftServerInfos serverInfos, int port)
    {
        this.instance = instance;
        this.serverInfos = serverInfos;
        this.serverFolder = new File(this.instance.getServerFolder(), this.serverInfos.getServerName());
        this.port = port;
    }

    public boolean makeServer()
    {
        try
        {
            this.instance.getLinuxBridge().mkdir(this.serverFolder.getAbsolutePath());

            this.instance.getResourceManager().downloadServer(this, this.serverFolder);
            this.instance.getResourceManager().downloadMap(this, this.serverFolder);
            this.instance.getResourceManager().downloadDependencies(this, this.serverFolder);
            this.instance.getResourceManager().patchServer(this, this.serverFolder);
        }
        catch (Exception e)
        {
            this.instance.log(Level.SEVERE, "Can't make the server " + this.serverInfos.getServerName() + "!");
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public boolean startServer()
    {
        try
        {
            this.instance.getLinuxBridge().mark2Start(this.serverFolder.getAbsolutePath());
        }
        catch (Exception e)
        {
            this.instance.log(Level.SEVERE, "Can't start the server " + this.serverInfos.getServerName() + "!");
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public boolean stopServer()
    {
        try
        {
            this.instance.getLinuxBridge().mark2Stop(this.serverInfos.getServerName());
        }
        catch (Exception e)
        {
            this.instance.log(Level.SEVERE, "Can't stop the server " + this.serverInfos.getServerName() + "!");
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public MinecraftServerInfos getServerInfos()
    {
        return this.serverInfos;
    }

    public File getServerFolder()
    {
        return this.serverFolder;
    }

    public int getPort()
    {
        return this.port;
    }
}
