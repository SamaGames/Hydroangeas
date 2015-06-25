package net.samagames.hydroangeas;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import joptsimple.OptionSet;
import net.samagames.hydroangeas.utils.MiscUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;

public class Configuration
{
    private final Hydroangeas instance;
    private JsonObject jsonConfiguration;

    public String redisIp;
    public String redisPassword;
    public int redisPort;

    public Configuration(Hydroangeas instance, OptionSet options)
    {
        this.instance = instance;

        if(options.has("d"))
            this.createDefaultConfiguration();

        try
        {
            this.loadConfiguration(options.valueOf("c").toString());
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void loadConfiguration(String path) throws FileNotFoundException
    {
        this.instance.log(Level.INFO, "Configuration file is: " + path);
        File configurationFile = new File(path);

        if(!configurationFile.exists())
        {
            this.instance.log(Level.SEVERE, "Configuration file don't exist!");
            System.exit(-1);
        }

        JsonObject jsonRoot = new JsonParser().parse(new FileReader(new File(path))).getAsJsonObject();
        this.jsonConfiguration = jsonRoot;

        if(!validateJson(jsonRoot))
        {
            this.instance.log(Level.SEVERE, "Configuration file isn't valid! Please just modify the default configuration file!");
            System.exit(-1);
        }

        this.redisIp = jsonRoot.get("redis-ip").getAsString();
        this.redisPort = jsonRoot.get("redis-port").getAsInt();
        this.redisPassword = jsonRoot.get("redis-password").getAsString();
    }

    public void createDefaultConfiguration()
    {
        try
        {
            File destinationFile = new File(MiscUtils.getJarFolder(), "config.json");
            FileUtils.copyURLToFile(Configuration.class.getResource("/config.json"), destinationFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        this.instance.log(Level.INFO, "Default configuration file created.");
        System.exit(0);
    }

    public JsonObject getJsonConfiguration()
    {
        return this.jsonConfiguration;
    }

    public boolean validateJson(JsonObject object)
    {
        boolean flag = true;

        /** Common **/
        if(!object.has("redis-ip")) flag = false;
        if(!object.has("redis-port")) flag = false;
        if(!object.has("redis-password")) flag = false;
        if(!object.has("web-domain")) flag = false;

        /** Client **/
        if(!object.has("max-weight")) flag = false;

        return flag;
    }
}
