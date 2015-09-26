package net.samagames.hydroangeas.client.resources;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;
import net.samagames.hydroangeas.client.servers.ServerDependency;
import net.samagames.hydroangeas.common.protocol.intranet.MinecraftServerIssuePacket;
import net.samagames.hydroangeas.utils.NetworkUtils;
import org.apache.commons.io.FileUtils;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import java.io.*;

public class ResourceManager
{
    private final HydroangeasClient instance;

    private CacheManager cacheManager;

    public ResourceManager(HydroangeasClient instance)
    {
        this.instance = instance;

        this.cacheManager = new CacheManager(instance);
    }

    public void downloadServer(MinecraftServerC server, File serverPath) throws IOException
    {
        String existURL = this.instance.getTemplatesDomain() + "servers/exist.php?game=" + server.getGame();
        boolean exist = Boolean.valueOf(NetworkUtils.readURL(existURL));

        if (!exist)
        {
            throw new IllegalStateException("Server template don't exist!");
        }

        File dest = new File(serverPath, server.getGame() + ".tar.gz");

        FileUtils.copyFile(cacheManager.getServerFiles(server.getGame()), dest);

        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
        archiver.extract(dest, serverPath.getAbsoluteFile());
    }

    public void downloadMap(MinecraftServerC server, File serverPath) throws IOException
    {
        try
        {
            String existURL = this.instance.getTemplatesDomain() + "maps/exist.php?game=" + server.getGame() + "&map=" + server.getMap().replaceAll(" ", "_");
            boolean exist = Boolean.valueOf(NetworkUtils.readURL(existURL));

            if (!exist)
            {
                throw new IllegalStateException("Server's map don't exist! (" + existURL + ")");
            }

            File dest = new File(serverPath, server.getGame() + "_" + server.getMap().replaceAll(" ", "_") + ".tar.gz");

            FileUtils.copyFile(cacheManager.getMapFiles(server.getGame(), server.getMap().replaceAll(" ", "_")), dest);

            Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
            archiver.extract(dest, serverPath.getAbsoluteFile());
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public void downloadDependencies(MinecraftServerC server, File serverPath) throws IOException
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

    public void downloadDependency(MinecraftServerC server, ServerDependency dependency, File serverPath) throws IOException
    {
        String existURL = this.instance.getTemplatesDomain() + "dependencies/exist.php?name=" + dependency.getName() + "&version=" + dependency.getVersion();
        File pluginsPath = new File(serverPath, "plugins/");

        if(!pluginsPath.exists())
            pluginsPath.mkdirs();

        if (!Boolean.parseBoolean(NetworkUtils.readURL(existURL)))
        {
            throw new IllegalStateException("Servers' dependency '" + dependency.getName() + "' don't exist!");
        }

        File dest = new File(pluginsPath, dependency.getName() + "_" + dependency.getVersion() + ".tar.gz");

        FileUtils.copyFile(cacheManager.getDebFiles(dependency.getName(), dependency.getVersion()), dest);

        ArchiverFactory.createArchiver("tar", "gz").extract(dest, pluginsPath.getAbsoluteFile());
    }

    public void patchServer(MinecraftServerC server, File serverPath, boolean isCoupaingServer) throws IOException
    {
        this.instance.getLinuxBridge().sed("%serverName%", server.getServerName(), new File(serverPath, "plugins" + File.separator + "SamaGamesAPI" + File.separator + "config.yml").getAbsolutePath());
        this.instance.getLinuxBridge().sed("%serverPort%", String.valueOf(server.getPort()), new File(serverPath, "server.properties").getAbsolutePath());
        this.instance.getLinuxBridge().sed("%serverIp%", instance.getAsClient().getIP(), new File(serverPath, "server.properties").getAbsolutePath());
        this.instance.getLinuxBridge().sed("%serverName%", server.getServerName(), new File(serverPath, "scripts.txt").getAbsolutePath());

        File gameFile = new File(serverPath, "game.json");
        gameFile.createNewFile();

        JsonObject rootJson = new JsonObject();
        rootJson.addProperty("map-name", server.getMap());
        rootJson.addProperty("min-slots", server.getMinSlot());
        rootJson.addProperty("max-slots", server.getMaxSlot());

        rootJson.add("options", server.getOptions());


        FileOutputStream fOut = new FileOutputStream(gameFile);
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
        myOutWriter.append(new Gson().toJson(rootJson));
        myOutWriter.close();
        fOut.close();
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }
}