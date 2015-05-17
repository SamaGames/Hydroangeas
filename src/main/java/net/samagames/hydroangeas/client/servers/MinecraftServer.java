package net.samagames.hydroangeas.client.servers;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.common.informations.MinecraftServerInfos;
import net.samagames.hydroangeas.utils.InternetUtils;
import net.samagames.hydroangeas.utils.LinuxBridge;

import java.io.File;
import java.util.logging.Level;

public class MinecraftServer
{
    private final HydroangeasClient instance;
    private final MinecraftServerInfos serverInfos;
    private final File serverFolder;

    public MinecraftServer(HydroangeasClient instance, MinecraftServerInfos serverInfos)
    {
        this.instance = instance;
        this.serverInfos = serverInfos;
        this.serverFolder = new File(this.instance.getServerFolder(), this.serverInfos.getServerName());
    }

    public boolean makeServer()
    {
        String existURL = this.instance.getTemplatesDomain() + "exist.php?game=" + this.serverInfos.getGame() + "&map=" + this.serverInfos.getMap();
        boolean existResponse = Boolean.valueOf(InternetUtils.readURL(existURL));

        String wgetURL = this.instance.getTemplatesDomain() + this.serverInfos.getGame() + "_" + this.serverInfos.getMap() + ".tar.gz";

        if(!existResponse)
        {
            this.instance.log(Level.SEVERE, "Can't get the server template for the game '" + this.serverInfos.getGame() + "' and the map '" + this.serverInfos.getMap() + "'!");
            return false;
        }

        try
        {
            LinuxBridge.mkdir(this.serverFolder.getAbsolutePath());
            LinuxBridge.wget(wgetURL, this.serverFolder.getAbsolutePath());
            LinuxBridge.gzipExtract(new File(this.serverFolder, this.serverInfos.getGame() + "_" + this.serverInfos.getMap() + ".tar.gz").getAbsolutePath(), this.serverFolder.getAbsolutePath());
        }
        catch (Exception e)
        {
            this.instance.log(Level.SEVERE, "Can't setup the server " + this.serverInfos.getServerName() + "!");
            return false;
        }

        return true;
    }

    public boolean startServer()
    {
        try
        {
            LinuxBridge.bash(new File(this.serverFolder, "start.sh").getAbsolutePath());
        }
        catch (Exception e)
        {
            this.instance.log(Level.SEVERE, "Can't start the server " + this.serverInfos.getServerName() + "!");
            return false;
        }

        return true;
    }

    public boolean stopServer()
    {
        try
        {
            LinuxBridge.screenKill(this.serverInfos.getServerName());
        }
        catch (Exception e)
        {
            this.instance.log(Level.SEVERE, "Can't stop the server " + this.serverInfos.getServerName() + "!");
            return false;
        }

        return true;
    }

    public MinecraftServerInfos getServerInfos()
    {
        return this.serverInfos;
    }
}
