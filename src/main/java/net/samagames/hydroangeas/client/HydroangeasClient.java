package net.samagames.hydroangeas.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import joptsimple.OptionSet;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.commands.ClientCommandManager;
import net.samagames.hydroangeas.client.docker.DockerAPI;
import net.samagames.hydroangeas.client.receiver.ServiceRequestReceiver;
import net.samagames.hydroangeas.client.resources.LogManager;
import net.samagames.hydroangeas.client.resources.ResourceManager;
import net.samagames.hydroangeas.client.servers.ServerManager;
import net.samagames.hydroangeas.client.tasks.LifeThread;
import net.samagames.hydroangeas.client.tasks.ServerAliveWatchDog;
import net.samagames.hydroangeas.common.protocol.intranet.ByeFromClientPacket;
import net.samagames.hydroangeas.utils.MiscUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/*
 * This file is part of Hydroangeas.
 *
 * Hydroangeas is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hydroangeas is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hydroangeas.  If not, see <http://www.gnu.org/licenses/>.
 */
public class HydroangeasClient extends Hydroangeas
{
    private String templatesDomain;
    private int maxWeight;
    private File serverFolder;

    private RestrictionMode restrictionMode;
    private List<String> whitelist;
    private List<String> blacklist;

    private ClientConnectionManager connectionManager;
    private LifeThread lifeThread;
    private ServerManager serverManager;
    private ResourceManager resourceManager;
    private LogManager logManager;

    private DockerAPI dockerAPI;
    private ServerAliveWatchDog serverAliveWatchDog;

    private JsonObject dockerConfig;

    public HydroangeasClient(OptionSet options) throws IOException
    {
        super(options);
        dockerAPI = new DockerAPI();
    }

    @Override
    public void enable()
    {
        this.log(Level.INFO, "Starting Hydroangeas client...");

        this.loadConfig();

        serverFolder.mkdir();

        logManager = new LogManager(MiscUtils.getJarFolder());

        try
        {
            FileUtils.forceDelete(serverFolder);
            FileUtils.forceMkdir(serverFolder);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        connectionManager = new ClientConnectionManager(this);
        this.redisSubscriber.registerReceiver("samaconnect.services.requests", new ServiceRequestReceiver(this));

        commandManager = new ClientCommandManager(this);

        this.redisSubscriber.registerReceiver("global@" + getUUID() + "@hydroangeas-client", connectionManager::getPacket);
        this.redisSubscriber.registerReceiver("globalSecurity@hydroangeas-client", connectionManager::getPacket);

        this.serverManager = new ServerManager(this);
        this.resourceManager = new ResourceManager(this);

        this.lifeThread = new LifeThread(this);
        this.lifeThread.start();

        this.serverAliveWatchDog = new ServerAliveWatchDog(this);
    }

    @Override
    public void loadConfig()
    {
        super.loadConfig();

        JsonElement parsed = null;
        try {
            File f = new File("DockerConfig.json");
            f.createNewFile();
            InputStreamReader reader = new InputStreamReader(new FileInputStream(f), "UTF-8");
            parsed = new JsonParser().parse(reader);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(parsed == null)
            dockerConfig = new JsonObject();
        else
            dockerConfig = parsed.getAsJsonObject();

        blacklist = new ArrayList<>();
        whitelist = new ArrayList<>();

        this.templatesDomain = this.configuration.getJsonConfiguration().get("web-domain").getAsString();
        this.maxWeight = this.configuration.getJsonConfiguration().get("max-weight").getAsInt();
        this.serverFolder = new File(MiscUtils.getJarFolder(), "servers");

        try{
            this.restrictionMode = RestrictionMode.valueFrom(configuration.getJsonConfiguration().get("RestrictionMode").getAsString());
            getLogger().info("Server restriction is set to: " + restrictionMode.getMode());
        }catch (Exception e)
        {
            this.restrictionMode = RestrictionMode.NONE;
            getLogger().warning("Restriction mode not set ! Default: none");
        }

        try{
            for(JsonElement data : configuration.getJsonConfiguration().get("Whitelist").getAsJsonArray())
            {
                String templateID = data.getAsString();
                if(templateID != null)
                {
                    whitelist.add(templateID);
                    getLogger().info("Adding to whitelist: " + templateID);
                }
            }
        }catch(Exception e)
        {
            getLogger().info("No whitelist load !");
        }

        try{
            for(JsonElement data : configuration.getJsonConfiguration().get("Blacklist").getAsJsonArray())
            {
                String templateID = data.getAsString();
                if(templateID != null)
                {
                    blacklist.add(templateID);
                    getLogger().info("Adding to blacklist: " + templateID);
                }
            }
        }catch(Exception e)
        {
            getLogger().info("No blacklist load !");
        }

        try {
            if(lifeThread != null)
            {
                lifeThread.sendData(true);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void disable()
    {
        connectionManager.sendPacket(new ByeFromClientPacket(getUUID()));
        this.serverManager.stopAll();
        this.serverAliveWatchDog.disable();
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

    public String getSimpleTemplatesDomain()
    {
        return this.templatesDomain;
    }

    public String getTemplatesDomain()
    {
        return this.templatesDomain + "static/templates/";
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

    public List<String> getWhitelist()
    {
        return whitelist;
    }

    public List<String> getBlacklist()
    {
        return blacklist;
    }

    public RestrictionMode getRestrictionMode() {
        return restrictionMode;
    }

    public void setRestrictionMode(RestrictionMode restrictionMode) {
        this.restrictionMode = restrictionMode;
    }

    public LogManager getLogManager() {
        return logManager;
    }

    public DockerAPI getDockerAPI() {
        return dockerAPI;
    }

    public ServerAliveWatchDog getServerAliveWatchDog() {
        return serverAliveWatchDog;
    }

    public JsonObject getDockerConfig() {
        return dockerConfig;
    }
}
