package net.samagames.hydroangeas.client.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;
import net.samagames.hydroangeas.client.servers.ServerDependency;
import net.samagames.hydroangeas.utils.NetworkUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.FileType;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class ResourceManager
{
    private static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();
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

        InputStreamReader fileReader = null;
        try
        {
            fileReader = new InputStreamReader(new FileInputStream(dependenciesFile), "UTF-8");
            List<ServerDependency> dependencies = GSON.fromJson(fileReader, new TypeToken<List<ServerDependency>>()
            {
            }.getType());
            for (ServerDependency dependency : dependencies)
            {
                this.downloadDependency(server, dependency, serverPath);
            }
        } finally
        {
            try
            {
                if (fileReader != null)
                    fileReader.close();
            } catch (IOException e)
            {

            }
        }
    }

    public void downloadDependency(MinecraftServerC server, ServerDependency dependency, File serverPath) throws IOException
    {
        String existURL = this.instance.getTemplatesDomain() + "dependencies/exist.php?name=" + dependency.getName() + "&version=" + dependency.getVersion() + "&ext=" + dependency.getExt();
        File pluginsPath = new File(serverPath, "plugins");

        if (!pluginsPath.exists())
            FileUtils.forceMkdir(pluginsPath);

        if (!Boolean.parseBoolean(NetworkUtils.readURL(existURL)))
        {
            throw new IllegalStateException("Servers' dependency '" + dependency.getName() + "' don't exist!");
        }

        File dest;
        if (dependency.getType().equals("server") && !dependency.isExtractable())
        {
            dest = new File(serverPath, "spigot.jar");
            if (dest.exists())
                FileUtils.deleteQuietly(dest);
        } else
        {
            dest = new File(pluginsPath, dependency.getName() + "-" + dependency.getVersion() + "." + dependency.getExt());
        }

        FileUtils.copyFile(cacheManager.getDebFiles(dependency), dest);

        if (dependency.isExtractable())
            ArchiverFactory.createArchiver(FileType.get(dest)).extract(dest, pluginsPath.getAbsoluteFile());
    }

    public void patchServer(MinecraftServerC server, File serverPath, boolean isCoupaingServer) throws IOException
    {
        FileOutputStream outputStream = null;

        try
        {
            // Generate API configuration
            File apiConfiguration = new File(serverPath, "plugins" + File.separator + "SamaGamesAPI" + File.separator + "config.yml");
            FileUtils.deleteQuietly(apiConfiguration);
            FileUtils.forceMkdir(apiConfiguration.getParentFile());
            apiConfiguration.createNewFile();
            outputStream = new FileOutputStream(apiConfiguration);
            outputStream.write(("bungeename: " + server.getServerName()).getBytes(Charset.forName("UTF-8")));
            outputStream.flush();

            // Generate data.yml
            File credentialsFile = new File(serverPath, "data.yml");
            FileUtils.deleteQuietly(credentialsFile);
            outputStream.close();
            outputStream = new FileOutputStream(credentialsFile);
            outputStream.write(("redis-bungee-ip: " + Hydroangeas.getInstance().getConfiguration().redisIp).getBytes(Charset.forName("UTF-8")));
            outputStream.write(System.getProperty("line.separator").getBytes());
            outputStream.write(("redis-bungee-port: " + Hydroangeas.getInstance().getConfiguration().redisPort).getBytes(Charset.forName("UTF-8")));
            outputStream.write(System.getProperty("line.separator").getBytes());
            outputStream.write(("redis-bungee-password: " + Hydroangeas.getInstance().getConfiguration().redisPassword).getBytes(Charset.forName("UTF-8")));
            outputStream.write(System.getProperty("line.separator").getBytes());

            outputStream.write(("sql-ip: " + Hydroangeas.getInstance().getConfiguration().sqlURL).getBytes(Charset.forName("UTF-8")));
            outputStream.write(System.getProperty("line.separator").getBytes());
            outputStream.write(("sql-user: " + Hydroangeas.getInstance().getConfiguration().sqlUser).getBytes(Charset.forName("UTF-8")));
            outputStream.write(System.getProperty("line.separator").getBytes());
            outputStream.write(("sql-pass: " + Hydroangeas.getInstance().getConfiguration().sqlPassword).getBytes(Charset.forName("UTF-8")));
            outputStream.write(System.getProperty("line.separator").getBytes());
            outputStream.flush();
            outputStream.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        this.instance.getLinuxBridge().sed("%serverPort%", String.valueOf(server.getPort()), new File(serverPath, "server.properties").getAbsolutePath());
        this.instance.getLinuxBridge().sed("%serverIp%", instance.getAsClient().getIP(), new File(serverPath, "server.properties").getAbsolutePath());
        this.instance.getLinuxBridge().sed("%serverName%", server.getServerName(), new File(serverPath, "scripts.txt").getAbsolutePath());

        File gameFile = new File(serverPath, "game.json");
        gameFile.createNewFile();

        JsonObject rootJson = new JsonObject();
        rootJson.addProperty("template-id", server.getTemplateID());
        rootJson.addProperty("map-name", server.getMap());
        rootJson.addProperty("min-slots", server.getMinSlot());
        rootJson.addProperty("max-slots", server.getMaxSlot());

        rootJson.add("options", server.getOptions());

        FileOutputStream fOut = null;
        try
        {
            fOut = new FileOutputStream(gameFile);
            fOut.write(new Gson().toJson(rootJson).getBytes(Charset.forName("UTF-8")));
            fOut.flush();
        } finally
        {
            try
            {
                if (fOut != null)
                    fOut.close();
            } catch (IOException e)
            {

            }
        }
    }

    public void extract(File dir ) throws IOException {
        File listDir[] = dir.listFiles();
        if (listDir.length!=0){
            for (File i:listDir){
        /*  Warning! this will try and extract all files in the directory
            if other files exist, a for loop needs to go here to check that
            the file (i) is an archive file before proceeding */
                if (i.isDirectory()){
                    break;
                }
                String fileName = i.toString();
                String tarFileName = fileName +".tar";
                FileInputStream instream= new FileInputStream(fileName);
                GZIPInputStream ginstream =new GZIPInputStream(instream);
                FileOutputStream outstream = new FileOutputStream(tarFileName);
                byte[] buf = new byte[1024];
                int len;
                while ((len = ginstream.read(buf)) > 0)
                {
                    outstream.write(buf, 0, len);
                }
                ginstream.close();
                outstream.close();
                //There should now be tar files in the directory
                //extract specific files from tar
                TarArchiveInputStream myTarFile=new TarArchiveInputStream(new FileInputStream(tarFileName));
                TarArchiveEntry entry = null;
                int offset;
                FileOutputStream outputFile=null;
                //read every single entry in TAR file
                while ((entry = myTarFile.getNextTarEntry()) != null) {
                    //the following two lines remove the .tar.gz extension for the folder name
                    fileName = i.getName().substring(0, i.getName().lastIndexOf('.'));
                    fileName = fileName.substring(0, fileName.lastIndexOf('.'));
                    File outputDir =  new File(i.getParent() + "/" + fileName + "/" + entry.getName());
                    if(! outputDir.getParentFile().exists()){
                        outputDir.getParentFile().mkdirs();
                    }
                    //if the entry in the tar is a directory, it needs to be created, only files can be extracted
                    if(entry.isDirectory()){
                        outputDir.mkdirs();
                    }else{
                        byte[] content = new byte[(int) entry.getSize()];
                        offset=0;
                        myTarFile.read(content, offset, content.length - offset);
                        outputFile=new FileOutputStream(outputDir);
                        IOUtils.write(content,outputFile);
                        outputFile.close();
                    }
                }
                //close and delete the tar files, leaving the original .tar.gz and the extracted folders
                myTarFile.close();
                File tarFile =  new File(tarFileName);
                tarFile.delete();
            }
        }
    }

    public CacheManager getCacheManager()
    {
        return cacheManager;
    }
}