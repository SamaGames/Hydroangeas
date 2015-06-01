package net.samagames.hydroangeas.client;

import joptsimple.OptionSet;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.packets.HelloServerPacketReceiver;
import net.samagames.hydroangeas.client.packets.MinecraftServerReceiver;
import net.samagames.hydroangeas.client.schedulers.LifeThread;
import net.samagames.hydroangeas.client.servers.ResourceManager;
import net.samagames.hydroangeas.client.servers.ServerManager;
import net.samagames.hydroangeas.utils.JsonUtils;
import net.samagames.hydroangeas.utils.MiscUtils;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;

public class HydroangeasClient extends Hydroangeas
{
    private UUID clientUUID;
    private String dedicatedGame;
    private String templatesDomain;
    private File serverFolder;

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
        this.dedicatedGame = JsonUtils.getStringOrNull(this.configuration.getJsonConfiguration().get("dedicated-game"));
        this.templatesDomain = this.configuration.getJsonConfiguration().get("templates-domain").getAsString();
        this.serverFolder = new File(MiscUtils.getJarFolder(), "servers");

        if(!this.serverFolder.exists())
            this.serverFolder.mkdirs();

        this.redisSubscriber.registerReceiver("hello@" + this.clientUUID.toString() + "@hydroangeas-client", new HelloServerPacketReceiver());
        this.redisSubscriber.registerReceiver("server@" + this.clientUUID.toString() + "@hydroangeas-client", new MinecraftServerReceiver());

        this.lifeThread = new LifeThread(this);
        this.lifeThread.start();

        this.serverManager = new ServerManager(this);
        this.resourceManager = new ResourceManager(this);
    }

    @Override
    public void shutdown()
    {
        super.shutdown();

        this.lifeThread.stop();
        this.serverManager.stopAll();
    }

    public UUID getClientUUID()
    {
        return this.clientUUID;
    }

    public String getDedicatedGame()
    {
        return this.dedicatedGame;
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

    public ResourceManager getResourceManager()
    {
        return this.resourceManager;
    }
}
