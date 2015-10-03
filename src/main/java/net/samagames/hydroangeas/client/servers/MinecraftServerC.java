package net.samagames.hydroangeas.client.servers;

import com.google.gson.JsonElement;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.tasks.ServerThread;
import net.samagames.hydroangeas.common.protocol.intranet.MinecraftServerIssuePacket;
import net.samagames.hydroangeas.common.protocol.intranet.MinecraftServerOrderPacket;
import net.samagames.hydroangeas.utils.MiscUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class MinecraftServerC
{
    private final HydroangeasClient instance;

    private final UUID uuid;
    private final boolean coupaingServer;
    private final File serverFolder;
    private String game;
    private String map;
    private int minSlot;
    private int maxSlot;
    private JsonElement options;
    private int port;

    private Integer hubID;

    private int weight;

    private ServerThread serverThread;

    public MinecraftServerC(HydroangeasClient instance, MinecraftServerOrderPacket serverInfos, int port)
    {
        this.instance = instance;

        this.uuid = serverInfos.getUUID();
        this.coupaingServer = serverInfos.isCoupaingServer();

        this.hubID = serverInfos.getHubID();

        this.game = serverInfos.getGame();
        this.map = serverInfos.getMap();
        this.minSlot = serverInfos.getMinSlot();
        this.maxSlot = serverInfos.getMaxSlot();

        options = serverInfos.getOptions();

        this.serverFolder = new File(this.instance.getServerFolder(), serverInfos.getServerName());
        this.serverFolder.deleteOnExit();
        this.port = port;

        this.weight = MiscUtils.calculServerWeight(game, maxSlot, isCoupaingServer());
    }

    public boolean makeServer()
    {
        try
        {
            this.serverFolder.mkdir();
            this.instance.getResourceManager().downloadServer(this, this.serverFolder);
            this.instance.getResourceManager().downloadMap(this, this.serverFolder);
            this.instance.getResourceManager().downloadDependencies(this, this.serverFolder);
        } catch (Exception e)
        {
            this.instance.log(Level.SEVERE, "Can't make the server " + getServerName() + "!");
            instance.getConnectionManager().sendPacket(new MinecraftServerIssuePacket(this.instance.getClientUUID(), this.getServerName(), MinecraftServerIssuePacket.Type.MAKE));
            e.printStackTrace();
            serverFolder.delete();
            return false;
        }

        try
        {
            this.instance.getResourceManager().patchServer(this, this.serverFolder, isCoupaingServer());
        } catch (IOException e)
        {
            instance.getConnectionManager().sendPacket(new MinecraftServerIssuePacket(this.instance.getClientUUID(), this.getServerName(), MinecraftServerIssuePacket.Type.PATCH));
            e.printStackTrace();
            serverFolder.delete();
            return false;
        }

        return true;
    }

    public boolean startServer()
    {
        try
        {
            serverThread = new ServerThread(this,
                    new String[]{"java",
                            "-Xmx1152M",
                            "-Xms512M",
                            "-Xmn256M",
                            "-XX:-OmitStackTraceInFastThrow",
                            "-XX:SurvivorRatio=2",
                            "-XX:-UseAdaptiveSizePolicy",
                            "-XX:+UseConcMarkSweepGC",
                            "-XX:+CMSConcurrentMTEnabled",
                            "-XX:+CMSParallelRemarkEnabled",
                            "-XX:+CMSParallelSurvivorRemarkEnabled",
                            "-XX:CMSMaxAbortablePrecleanTime=10000",
                            "-XX:+UseCMSInitiatingOccupancyOnly",
                            "-XX:CMSInitiatingOccupancyFraction=63",
                            "-XX:+UseParNewGC",
                            "-Xnoclassgc",
                            "-jar", "spigot.jar", "nogui"},
                    new String[]{""}, serverFolder);
            serverThread.start();
            instance.getLogger().info("Starting server " + getServerName());
        } catch (Exception e)
        {
            this.instance.log(Level.SEVERE, "Can't start the server " + getServerName() + "!");
            e.printStackTrace();
            serverFolder.delete();

            return false;
        }

        return true;
    }

    public boolean stopServer()
    {
        try
        {
            serverThread.forceStop();
            serverFolder.delete();
        } catch (Exception e)
        {
            this.instance.log(Level.SEVERE, "Can't stop the server " + getServerName() + "!");
            e.printStackTrace();
            serverFolder.delete();
            return false;
        }
        return true;
    }

    public File getServerFolder()
    {
        return this.serverFolder;
    }

    public int getWeight()
    {
        return weight;
    }

    public int getPort()
    {
        return this.port;
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public String getGame()
    {
        return this.game;
    }

    public String getMap()
    {
        return this.map;
    }

    public String getServerName()
    {
        return this.game + "_" + ((hubID == null)?this.uuid.toString().split("-")[0]: hubID);
    }

    public int getMinSlot()
    {
        return this.minSlot;
    }

    public int getMaxSlot()
    {
        return this.maxSlot;
    }

    public JsonElement getOptions()
    {
        return this.options;
    }

    public boolean isCoupaingServer()
    {
        return this.coupaingServer;
    }

    public HydroangeasClient getInstance()
    {
        return instance;
    }

    public boolean isHub() {
        return hubID != null;
    }

    public Integer getHubID() {
        return hubID;
    }
}
