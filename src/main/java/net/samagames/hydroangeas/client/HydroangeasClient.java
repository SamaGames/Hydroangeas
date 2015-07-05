package net.samagames.hydroangeas.client;

import joptsimple.OptionSet;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.schedulers.LifeThread;
import net.samagames.hydroangeas.client.servers.ResourceManager;
import net.samagames.hydroangeas.client.servers.ServerManager;
import net.samagames.hydroangeas.common.protocol.ByeFromClientPacket;
import net.samagames.hydroangeas.utils.InternetUtils;
import net.samagames.hydroangeas.utils.MiscUtils;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;

public class HydroangeasClient extends Hydroangeas
{
    private UUID clientUUID;
    private String templatesDomain;
    private int maxWeight;
    private File serverFolder;

    private ClientConnectionManager connectionManager;
    private LifeThread lifeThread;
    private ServerManager serverManager;
    private ResourceManager resourceManager;

    public HydroangeasClient(OptionSet options)
    {
        super(options);
    }

    @Override
    public void enable()
    {
        this.log(Level.INFO, "Starting Hydroangeas client...");

        this.clientUUID = UUID.randomUUID();
        this.templatesDomain = this.configuration.getJsonConfiguration().get("web-domain").getAsString() + "templates/";
        this.maxWeight = this.configuration.getJsonConfiguration().get("max-weight").getAsInt();
        this.serverFolder = new File(MiscUtils.getJarFolder(), "servers");

        if(!this.serverFolder.exists())
            this.serverFolder.mkdirs();

        connectionManager = new ClientConnectionManager(this);

        this.redisSubscriber.registerReceiver("global@" + this.clientUUID.toString() + "@hydroangeas-client", data -> connectionManager.getPacket(data));

        this.lifeThread = new LifeThread(this);
        this.lifeThread.start();

        this.serverManager = new ServerManager(this);
        this.resourceManager = new ResourceManager(this);
    }

    public void disable()
    {
        connectionManager.sendPacket(new ByeFromClientPacket(clientUUID));
        this.serverManager.stopAll();
    }

    public UUID getClientUUID()
    {
        return this.clientUUID;
    }

    public int getMaxWeight()
    {
        return this.maxWeight;
    }

    public int getActualWeight()
    {
        //TODO CACLUL WEIGHT
        return 0;
    }

    public String getTemplatesDomain()
    {
        return this.templatesDomain;
    }

    public File getServerFolder()
    {
        return this.serverFolder;
    }

    public LifeThread getLifeThread()
    {
        return this.lifeThread;
    }

    public ServerManager getServerManager()
    {
        return this.serverManager;
    }

    public String getIP()
    {
        return InternetUtils.getExternalIp();
    }

    public ClientConnectionManager getConnectionManager()
    {
        return connectionManager;
    }

    public ResourceManager getResourceManager()
    {
        return this.resourceManager;
    }
}
