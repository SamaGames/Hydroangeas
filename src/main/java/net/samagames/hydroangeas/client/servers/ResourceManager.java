package net.samagames.hydroangeas.client.servers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.packets.MinecraftServerIssuePacket;
import net.samagames.hydroangeas.utils.InternetUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ResourceManager
{
    private final HydroangeasClient instance;

    public ResourceManager(HydroangeasClient instance)
    {
        this.instance = instance;
    }

    public void downloadServer(MinecraftServer server, File serverPath)
    {
        try
        {
            String existURL = this.instance.getTemplatesDomain() + "servers/exist.php?game=" + server.getServerInfos().getGame();
            String wgetURL = this.instance.getTemplatesDomain() + "servers/" + server.getServerInfos().getGame() + ".tar.gz";
            boolean exist = Boolean.valueOf(InternetUtils.readURL(existURL));

            if (!exist)
            {
                new MinecraftServerIssuePacket(this.instance, server.getServerInfos(), MinecraftServerIssuePacket.Type.MAKE);
                throw new IllegalStateException("Server template don't exist!");
            }

            this.instance.getLinuxBridge().wget(wgetURL, serverPath.getAbsolutePath());
            this.instance.getLinuxBridge().gzipExtract(new File(serverPath, server.getServerInfos().getGame() + ".tar.gz").getAbsolutePath(), serverPath.getAbsolutePath());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new MinecraftServerIssuePacket(this.instance, server.getServerInfos(), MinecraftServerIssuePacket.Type.MAKE).send();
        }
    }

    public void downloadMap(MinecraftServer server, File serverPath)
    {
        try
        {
            String existURL = this.instance.getTemplatesDomain() + "maps/exist.php?game=" + server.getServerInfos().getGame() + "&map=" + server.getServerInfos().getMap();
            String wgetURL = this.instance.getTemplatesDomain() + "maps/" + server.getServerInfos().getGame() + "_" + server.getServerInfos().getMap() + ".tar.gz";
            boolean exist = Boolean.valueOf(InternetUtils.readURL(existURL));

            if (!exist)
            {
                new MinecraftServerIssuePacket(this.instance, server.getServerInfos(), MinecraftServerIssuePacket.Type.MAKE);
                throw new IllegalStateException("Server's map don't exist!");
            }

            this.instance.getLinuxBridge().wget(wgetURL, serverPath.getAbsolutePath());
            this.instance.getLinuxBridge().gzipExtract(new File(serverPath, server.getServerInfos().getGame() + "_" + server.getServerInfos().getMap() + ".tar.gz").getAbsolutePath(), serverPath.getAbsolutePath());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            new MinecraftServerIssuePacket(this.instance, server.getServerInfos(), MinecraftServerIssuePacket.Type.MAKE).send();
        }
    }

    public void downloadDependencies(MinecraftServer server, File serverPath)
    {
        try
        {
            File dependenciesFile = new File(serverPath, "dependencies.json");

            if(!dependenciesFile.exists())
                return;

            JsonArray jsonRoot = new JsonParser().parse(new FileReader(dependenciesFile)).getAsJsonArray();

            for(int i = 0; i < jsonRoot.size(); i++)
            {
                JsonObject jsonDependency = jsonRoot.get(i).getAsJsonObject();
                this.downloadDependency(server, new ServerDependency(jsonDependency.get("name").getAsString(), jsonDependency.get("version").getAsString()), serverPath);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void downloadDependency(MinecraftServer server, ServerDependency dependency, File serverPath)
    {
        try
        {
            String existURL = this.instance.getTemplatesDomain() + "dependencies/exist.php?name=" + dependency.getName() + "&version=" + dependency.getVersion();
            String wgetURL = this.instance.getTemplatesDomain() + "dependencies/" + dependency.getName() + "_" + dependency.getVersion() + ".tar.gz";
            File pluginsPath = new File(serverPath, "plugins");

            if(!pluginsPath.exists())
                pluginsPath.mkdirs();

            boolean exist = Boolean.valueOf(InternetUtils.readURL(existURL));

            if (!exist)
            {
                new MinecraftServerIssuePacket(this.instance, server.getServerInfos(), MinecraftServerIssuePacket.Type.MAKE);
                throw new IllegalStateException("Servers' dependency '" + dependency.getName() + "' don't exist!");
            }

            this.instance.getLinuxBridge().wget(wgetURL, pluginsPath.getAbsolutePath());
            this.instance.getLinuxBridge().gzipExtract(new File(pluginsPath, dependency.getName() + "_" + dependency.getVersion() + ".tar.gz").getAbsolutePath(), pluginsPath.getAbsolutePath());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            new MinecraftServerIssuePacket(this.instance, server.getServerInfos(), MinecraftServerIssuePacket.Type.MAKE).send();
        }
    }

    public void patchServer(MinecraftServer server, File serverPath)
    {
        try
        {
            this.instance.getLinuxBridge().sed("%serverName%", server.getServerInfos().getServerName(), new File(serverPath, "plugins" + File.separator + "SamaGamesAPI" + File.separator + "config.yml").getAbsolutePath());
            this.instance.getLinuxBridge().sed("%serverPort%", String.valueOf(server.getPort()), new File(serverPath, "server.properties").getAbsolutePath());
            this.instance.getLinuxBridge().sed("%serverIp%", InternetUtils.getExternalIp(), new File(serverPath, "server.properties").getAbsolutePath());
            this.instance.getLinuxBridge().sed("%serverName%", server.getServerInfos().getServerName(), new File(serverPath, "scripts.txt").getAbsolutePath());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new MinecraftServerIssuePacket(this.instance, server.getServerInfos(), MinecraftServerIssuePacket.Type.PATCH).send();
        }
    }
}