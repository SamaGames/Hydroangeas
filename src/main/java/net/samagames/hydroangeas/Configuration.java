package net.samagames.hydroangeas;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import joptsimple.OptionSet;
import net.samagames.hydroangeas.utils.MiscUtils;

import java.io.*;
import java.util.logging.Level;

public class Configuration
{
    private final Hydroangeas instance;

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
        File configFile = new File(MiscUtils.getJarFolder(), "config.json");
        JsonObject jsonRoot = new JsonObject();

        jsonRoot.addProperty("redis-ip", "0.0.0.0");
        jsonRoot.addProperty("redis-port", "6379");
        jsonRoot.addProperty("redis-password", "passw0rd");

        try
        {
            if(configFile.exists())
            {
                configFile.delete();
                configFile.createNewFile();
            }

            FileWriter writer = new FileWriter(configFile);
            writer.write(new Gson().toJson(jsonRoot));
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        this.instance.log(Level.INFO, "Default configuration file created.");
        System.exit(0);
    }

    public boolean validateJson(JsonObject object)
    {
        boolean flag = true;

        if(!object.has("redis-ip")) flag = false;
        if(!object.has("redis-port")) flag = false;
        if(!object.has("redis-password")) flag = false;

        return flag;
    }
}
