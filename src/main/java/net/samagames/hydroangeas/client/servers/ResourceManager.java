package net.samagames.hydroangeas.client.servers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.common.protocol.MinecraftServerIssuePacket;
import net.samagames.hydroangeas.utils.InternetUtils;
import net.samagames.hydroangeas.utils.ZipUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;

public class ResourceManager
{
    private final HydroangeasClient instance;

    public ResourceManager(HydroangeasClient instance)
    {
        this.instance = instance;
    }

    public void downloadServer(MinecraftServerC server, File serverPath)
    {
        try
        {
            String existURL = this.instance.getTemplatesDomain() + "servers/exist.php?game=" + server.getGame();
            String wgetURL = this.instance.getTemplatesDomain() + "servers/" + server.getGame() + ".tar.gz";
            boolean exist = Boolean.valueOf(InternetUtils.readURL(existURL));

            if (!exist)
            {
                instance.getConnectionManager().sendPacket(new MinecraftServerIssuePacket(this.instance.getClientUUID(), server.getServerName(), MinecraftServerIssuePacket.Type.MAKE));
                throw new IllegalStateException("Server template don't exist!");
            }

            FileUtils.copyURLToFile(new URL(wgetURL), new File(serverPath, server.getGame() + ".tar.gz"));
            ZipUtils.unGzip(new File(serverPath, server.getGame() + ".tar.gz"), serverPath.getAbsoluteFile());
            ZipUtils.unTar(new File(serverPath, server.getGame() + ".tar"), serverPath.getAbsoluteFile());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            instance.getConnectionManager().sendPacket(new MinecraftServerIssuePacket(this.instance.getClientUUID(), server.getServerName(), MinecraftServerIssuePacket.Type.MAKE));
        }
    }

    public void downloadMap(MinecraftServerC server, File serverPath)
    {
        try
        {
            String existURL = this.instance.getTemplatesDomain() + "maps/exist.php?game=" + server.getGame() + "&map=" + server.getMap();
            String wgetURL = this.instance.getTemplatesDomain() + "maps/" + server.getGame() + "_" + server.getMap() + ".tar.gz";
            boolean exist = Boolean.valueOf(InternetUtils.readURL(existURL));

            if (!exist)
            {
                new MinecraftServerIssuePacket(this.instance.getClientUUID(), server.getServerName(), MinecraftServerIssuePacket.Type.MAKE);
                throw new IllegalStateException("Server's map don't exist!");
            }

            FileUtils.copyURLToFile(new URL(wgetURL), new File(serverPath, server.getGame() + "_" + server.getMap() + ".tar.gz"));
            ZipUtils.unGzip(new File(serverPath, server.getGame() + "_" + server.getMap() + ".tar.gz"), serverPath.getAbsoluteFile());
            ZipUtils.unTar(new File(serverPath, server.getGame() + "_" + server.getMap() + ".tar"), serverPath.getAbsoluteFile());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            instance.getConnectionManager().sendPacket(new MinecraftServerIssuePacket(this.instance.getClientUUID(), server.getServerName(), MinecraftServerIssuePacket.Type.MAKE));
        }
    }

    public void downloadDependencies(MinecraftServerC server, File serverPath)
    {
        try
        {
            File dependenciesFile = new File(serverPath, "dependencies.json");

            while(!dependenciesFile.exists()) {}

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

    public void downloadDependency(MinecraftServerC server, ServerDependency dependency, File serverPath)
    {
        try
        {
            String existURL = this.instance.getTemplatesDomain() + "dependencies/exist.php?name=" + dependency.getName() + "&version=" + dependency.getVersion();
            String wgetURL = this.instance.getTemplatesDomain() + "dependencies/" + dependency.getName() + "_" + dependency.getVersion() + ".tar.gz";
            File pluginsPath = new File(serverPath, "plugins/");

            if(!pluginsPath.exists())
                pluginsPath.mkdirs();

            boolean exist = Boolean.valueOf(InternetUtils.readURL(existURL));

            if (!exist)
            {
                instance.getConnectionManager().sendPacket(new MinecraftServerIssuePacket(this.instance.getClientUUID(), server.getServerName(), MinecraftServerIssuePacket.Type.MAKE));
                throw new IllegalStateException("Servers' dependency '" + dependency.getName() + "' don't exist!");
            }

            FileUtils.copyURLToFile(new URL(wgetURL), new File(pluginsPath, dependency.getName() + "_" + dependency.getVersion() + ".tar.gz"));
            ZipUtils.unGzip(new File(pluginsPath, dependency.getName() + "_" + dependency.getVersion() + ".tar.gz"), pluginsPath.getAbsoluteFile());
            ZipUtils.unTar(new File(pluginsPath, dependency.getName() + "_" + dependency.getVersion() + ".tar"), pluginsPath.getAbsoluteFile());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            instance.getConnectionManager().sendPacket(new MinecraftServerIssuePacket(this.instance.getClientUUID(), server.getServerName(), MinecraftServerIssuePacket.Type.MAKE));
        }
    }

    public void patchServer(MinecraftServerC server, File serverPath, boolean isCoupaingServer)
    {
        try
        {
            this.instance.getLinuxBridge().sed("%serverName%", server.getServerName(), new File(serverPath, "plugins" + File.separator + "SamaGamesAPI" + File.separator + "config.yml").getAbsolutePath());
            this.instance.getLinuxBridge().sed("%serverPort%", String.valueOf(server.getPort()), new File(serverPath, "server.properties").getAbsolutePath());
            this.instance.getLinuxBridge().sed("%serverIp%", InternetUtils.getExternalIp(), new File(serverPath, "server.properties").getAbsolutePath());
            this.instance.getLinuxBridge().sed("%serverName%", server.getServerName(), new File(serverPath, "scripts.txt").getAbsolutePath());

            File coupaingFile = new File(serverPath, "game.json");
            coupaingFile.createNewFile();

            JsonObject rootJson = new JsonObject();
            rootJson.addProperty("map-name", server.getMap());
            rootJson.addProperty("min-slots", server.getMinSlot());
            rootJson.addProperty("max-slots", server.getMaxSlot());

            rootJson.add("options", server.getOptions());


            FileOutputStream fOut = new FileOutputStream(coupaingFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(new Gson().toJson(rootJson));
            myOutWriter.close();
            fOut.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            instance.getConnectionManager().sendPacket(new MinecraftServerIssuePacket(this.instance.getClientUUID(), server.getServerName(), MinecraftServerIssuePacket.Type.PATCH));
        }
    }
}