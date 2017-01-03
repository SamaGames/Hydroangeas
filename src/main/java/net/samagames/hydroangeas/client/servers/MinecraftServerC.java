package net.samagames.hydroangeas.client.servers;

import com.google.gson.JsonObject;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.docker.DockerContainer;
import net.samagames.hydroangeas.client.remote.RemoteControl;
import net.samagames.hydroangeas.common.data.MinecraftServer;
import net.samagames.hydroangeas.common.protocol.intranet.MinecraftServerIssuePacket;
import net.samagames.hydroangeas.common.protocol.intranet.MinecraftServerSyncPacket;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import static net.samagames.hydroangeas.Hydroangeas.getLogger;

public class MinecraftServerC extends MinecraftServer
{
    private final HydroangeasClient instance;

    private final File serverFolder;

    private long lastHeartbeat = System.currentTimeMillis();

    private DockerContainer container;

    private RemoteControl remoteControl;

    public MinecraftServerC(HydroangeasClient instance,
                            MinecraftServerSyncPacket serverInfos,
                            int port)
    {
        super(serverInfos.getMinecraftUUID(),
                serverInfos.getGame(),
                serverInfos.getMap(),
                serverInfos.getMinSlot(),
                serverInfos.getMaxSlot(),
                serverInfos.getOptions(),
                serverInfos.getStartupOptions()
                );
        this.instance = instance;

        this.coupaingServer = serverInfos.isCoupaingServer();

        this.hubID = serverInfos.getHubID();

        this.templateID = serverInfos.getTemplateID();

        this.timeToLive = serverInfos.getTimeToLive();

        this.serverFolder = new File(this.instance.getServerFolder(), serverInfos.getServerName());
        try
        {
            FileDeleteStrategy.FORCE.delete(serverFolder);
            FileUtils.forceDeleteOnExit(serverFolder);
        } catch (IOException e)
        {
            getLogger().warning(serverFolder + " will not be able to be deleted during JVM shutdown!");
        }
        this.port = port;

        this.weight = serverInfos.getWeight();
    }

    public boolean makeServer()
    {
        try
        {
            FileUtils.forceMkdir(serverFolder);
            this.instance.getResourceManager().downloadServer(this, this.serverFolder);
            this.instance.getResourceManager().downloadMap(this, this.serverFolder);
            this.instance.getResourceManager().downloadDependencies(this, this.serverFolder);
        } catch (Exception e)
        {
            this.instance.log(Level.SEVERE, "Can't make the server " + getServerName() + "!");
            instance.getConnectionManager().sendPacket(new MinecraftServerIssuePacket(this.instance.getClientUUID(), this.getServerName(), MinecraftServerIssuePacket.Type.MAKE));
            e.printStackTrace();
            try
            {
                FileDeleteStrategy.FORCE.delete(serverFolder);
                FileUtils.forceDeleteOnExit(serverFolder);
            } catch (IOException e1)
            {
                e1.printStackTrace();
            }
            return false;
        }

        try
        {
            this.instance.getResourceManager().patchServer(this, this.serverFolder, isCoupaingServer());
        } catch (Exception e)
        {
            instance.getConnectionManager().sendPacket(new MinecraftServerIssuePacket(this.instance.getClientUUID(), this.getServerName(), MinecraftServerIssuePacket.Type.PATCH));
            e.printStackTrace();
            try
            {
                FileUtils.forceDelete(serverFolder);
            } catch (IOException e1)
            {
                e1.printStackTrace();
            }
            return false;
        }

        return true;
    }

    public boolean startServer()
    {
        try
        {
            JsonObject startupOptionsObj = startupOptions.getAsJsonObject();
            String maxRAM = startupOptionsObj.get("maxRAM").getAsString();

            container = new DockerContainer(
                    getServerName(),
                    serverFolder,
                    port,
                    new String[]{"/usr/bin/java",
                            //"-Duser.dir " + serverFolder.getAbsolutePath(),
                            "-Xmx" + maxRAM,
                            "-Xms" + startupOptionsObj.get("minRAM").getAsString(),
                            "-Xmn" + startupOptionsObj.get("edenRAM").getAsString(),
                            "-XX:+UseG1GC",
                            "-XX:+UnlockExperimentalVMOptions",
                            "-XX:MaxGCPauseMillis=50",
                            "-XX:+DisableExplicitGC",
                            "-XX:G1HeapRegionSize=4M",
                            "-XX:TargetSurvivorRatio=90",
                            "-XX:G1NewSizePercent=50",
                            "-XX:G1MaxNewSizePercent=80",
                            "-XX:InitiatingHeapOccupancyPercent=10",
                            "-XX:G1MixedGCLiveThresholdPercent=50",
                            "-XX:+AggressiveOpts",
                            "-XX:+UseLargePagesInMetaspace",
                            "-Djava.net.preferIPv4Stack=true",
                            "-Dcom.sun.management.jmxremote",
                            "-Dcom.sun.management.jmxremote.port=" + (getPort()+1),
                            "-Dcom.sun.management.jmxremote.local.only=false",
                            "-Dcom.sun.management.jmxremote.authenticate=false",
                            "-Dcom.sun.management.jmxremote.ssl=false",
                            "-jar", serverFolder.getAbsolutePath()+"/spigot.jar", "nogui"},
                    maxRAM
            );
            Hydroangeas.getLogger().info(container.createContainer());

            getLogger().info("Starting server " + getServerName());

            remoteControl = new RemoteControl(this, "blackmesa", (getPort()+1));
        } catch (Exception e)
        {
            this.instance.log(Level.SEVERE, "Can't start the server " + getServerName() + "!");
            e.printStackTrace();
            try
            {
                FileDeleteStrategy.FORCE.delete(serverFolder);
                FileUtils.forceDelete(serverFolder);
            } catch (IOException e1)
            {
            }

            return false;
        }

        return true;
    }

    public boolean stopServer()
    {
        try
        {
            try{
                if (remoteControl != null)
                    remoteControl.disconnect();
            }catch (Exception ignored)
            {

            }

            instance.getServerManager().onServerStop(this);
            Hydroangeas.getInstance().getAsClient().getLogManager().saveLog(getServerName(), getTemplateID());
            container.removeContainer();

        } catch (Exception e)
        {
            this.instance.log(Level.SEVERE, "Can't stop the server " + getServerName() + "!");
            e.printStackTrace();
            return false;
        }finally {
            try
            {
                FileDeleteStrategy.FORCE.delete(serverFolder);
                FileUtils.forceDelete(serverFolder);
            } catch (IOException e1)
            {
            }
        }
        return true;
    }

    public File getServerFolder()
    {
        return this.serverFolder;
    }

    public HydroangeasClient getInstance()
    {
        return instance;
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void doHeartbeat()
    {
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public DockerContainer getContainer() {
        return container;
    }

    public RemoteControl getRemoteControl()
    {
        return remoteControl;
    }

}
