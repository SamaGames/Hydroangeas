package net.samagames.hydroangeas;

import com.google.gson.JsonObject;
import joptsimple.OptionSet;
import net.samagames.hydroangeas.utils.MiscUtils;

import java.io.File;
import java.util.logging.Level;

public class Configuration
{
    private final Hydroangeas instance;

    public Configuration(Hydroangeas instance, OptionSet options)
    {
        this.instance = instance;

        if(options.has("d"))
            this.createDefaultConfiguration();

        this.loadConfiguration(options.valueOf("c").toString());
    }

    public void loadConfiguration(String path)
    {
        this.instance.log(Level.INFO, "Configuration file is: " + path);
    }

    public void createDefaultConfiguration()
    {
        File configFile = new File(MiscUtils.getJarFolder(), "config.json");

        this.instance.log(Level.INFO, "Default configuration file created.");
        System.exit(0);
    }

    public boolean validateJson(JsonObject object)
    {
        return true;
    }
}
