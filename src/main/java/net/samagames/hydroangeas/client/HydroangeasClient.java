package net.samagames.hydroangeas.client;

import joptsimple.OptionSet;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.commands.ClientCommandManager;
import net.samagames.hydroangeas.client.resources.ResourceManager;
import net.samagames.hydroangeas.client.servers.ServerManager;
import net.samagames.hydroangeas.client.tasks.LifeThread;
import net.samagames.hydroangeas.common.protocol.intranet.ByeFromClientPacket;
import net.samagames.hydroangeas.utils.MiscUtils;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.UUID;
import java.util.logging.Level;

public class HydroangeasClient extends Hydroangeas
{
    private String templatesDomain;
    private int maxWeight;
    private File serverFolder;

    private ClientConnectionManager connectionManager;
    private LifeThread lifeThread;
    private ServerManager serverManager;
    private ResourceManager resourceManager;

    public HydroangeasClient(OptionSet options) throws IOException
    {
        super(options);
    }

    @Override
    public void enable()
    {
        this.log(Level.INFO, "Starting Hydroangeas client...");

        this.templatesDomain = this.configuration.getJsonConfiguration().get("web-domain").getAsString() + "templates/";
        this.maxWeight = this.configuration.getJsonConfiguration().get("max-weight").getAsInt();
        this.serverFolder = new File(MiscUtils.getJarFolder(), "servers");

        this.serverFolder.delete();
        this.serverFolder.mkdirs();

        connectionManager = new ClientConnectionManager(this);

        commandManager = new ClientCommandManager(this);

        this.redisSubscriber.registerReceiver("global@" + getUUID() + "@hydroangeas-client", connectionManager::getPacket);
        this.redisSubscriber.registerReceiver("globalSecurity@hydroangeas-client", connectionManager::getPacket);

        this.serverManager = new ServerManager(this);
        this.resourceManager = new ResourceManager(this);

        this.lifeThread = new LifeThread(this);
        this.lifeThread.start();
    }

    @Override
    public void disable()
    {
        connectionManager.sendPacket(new ByeFromClientPacket(getUUID()));
        this.serverManager.stopAll();
    }

    public UUID getClientUUID()
    {
        return getUUID();
    }

    public int getMaxWeight()
    {
        return this.maxWeight;
    }

    public int getActualWeight()
    {
        return serverManager.getWeightOfAllServers();
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
        try
        {
            return getInternalIpv4();
        } catch (IOException e)
        {
            return "0.0.0.0";
        }
    }

    private final String getInternalIpv4() throws IOException
    {
        NetworkInterface i = NetworkInterface.getByName("eth0");
        for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements(); )
        {
            InetAddress addr = (InetAddress) en2.nextElement();
            if (!addr.isLoopbackAddress())
            {
                if (addr instanceof Inet4Address)
                {
                    return addr.getHostAddress();
                }
            }
        }
        InetAddress inet = Inet4Address.getLocalHost();
        return inet == null ? "0.0.0.0" : inet.getHostAddress();
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
