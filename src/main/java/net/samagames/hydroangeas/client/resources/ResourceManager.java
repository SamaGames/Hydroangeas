package net.samagames.hydroangeas.client.resources;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;
import net.samagames.hydroangeas.client.servers.ServerDependency;
import net.samagames.hydroangeas.utils.NetworkUtils;
import org.apache.commons.io.FileUtils;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.FileType;

import java.io.*;
import java.util.List;

public class ResourceManager
{
    private final HydroangeasClient instance;
    private static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();

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
        String existURL = this.instance.getTemplatesDomain() + "maps/exist.php?game=" + server.getGame() + "&map=" + server.getMap().replaceAll(" ", "_");

        if (!Boolean.parseBoolean(NetworkUtils.readURL(existURL)))
        {
            throw new IllegalStateException("Server's map don't exist! (" + existURL + ")");
        }

        File dest = new File(serverPath, server.getGame() + "_" + server.getMap().replaceAll(" ", "_") + ".tar.gz");

        FileUtils.copyFile(cacheManager.getMapFiles(server.getGame(), server.getMap().replaceAll(" ", "_")), dest);

        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
        archiver.extract(dest, serverPath.getAbsoluteFile());
    }

    public void downloadDependencies(MinecraftServerC server, File serverPath) throws IOException
    {
        File dependenciesFile = new File(serverPath, "dependencies.json");

        List<ServerDependency> dependencies = GSON.fromJson(new FileReader(dependenciesFile), new TypeToken<List<ServerDependency>>() {}.getType());

        for (ServerDependency dependency : dependencies)
        {
            this.downloadDependency(server, dependency, serverPath);
        }
    }

    public void downloadDependency(MinecraftServerC server, ServerDependency dependency, File serverPath) throws IOException
    {
        String existURL = this.instance.getTemplatesDomain() + "dependencies/exist.php?name=" + dependency.getName() + "&version=" + dependency.getVersion() + "&ext=" + dependency.getExt();
        File pluginsPath = new File(serverPath, "plugins");

        if (!pluginsPath.exists())
            pluginsPath.mkdirs();

        if (!Boolean.parseBoolean(NetworkUtils.readURL(existURL)))
        {
            throw new IllegalStateException("Servers' dependency '" + dependency.getName() + "' don't exist!");
        }

        File dest;
        if (dependency.getType().equals("server") && !dependency.isExtractable())
        {
            dest = new File(serverPath, "spigot.jar");
            if (dest.exists())
                dest.delete();
        } else {
            dest = new File(pluginsPath, dependency.getName() + "-" + dependency.getVersion() + "." + dependency.getExt());
        }

        FileUtils.copyFile(cacheManager.getDebFiles(dependency), dest);

        if (dependency.isExtractable())
            ArchiverFactory.createArchiver(FileType.get(dest)).extract(dest, pluginsPath.getAbsoluteFile());
    }

    public void patchServer(MinecraftServerC server, File serverPath, boolean isCoupaingServer) throws IOException
    {

        File file = new File(serverPath, "plugins" + File.separator + "SamaGamesAPI" + File.separator + "config.yml");
        file.delete();
        file.getParentFile().mkdirs();
        file.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(("bungeename: " + server.getServerName()).getBytes());
        outputStream.close();

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
        fOut.write(new Gson().toJson(rootJson).getBytes());
        fOut.close();
    }

    public CacheManager getCacheManager()
    {
        return cacheManager;
    }
}