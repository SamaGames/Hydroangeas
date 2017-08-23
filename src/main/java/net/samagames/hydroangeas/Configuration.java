package net.samagames.hydroangeas;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import joptsimple.OptionSet;
import net.samagames.hydroangeas.utils.MiscUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
public class Configuration
{
    private final Hydroangeas instance;
    public String redisIp;
    public String redisPassword;
    public int redisPort;
    public String sqlURL;
    public String sqlUser;
    public String sqlPassword;
    private JsonObject jsonConfiguration;

    public Configuration(Hydroangeas instance, OptionSet options)
    {
        this.instance = instance;

        if (options.has("d"))
            this.createDefaultConfiguration();

        try
        {
            this.loadConfiguration(options.valueOf("c").toString());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void loadConfiguration(String path) throws IOException
    {
        this.instance.log(Level.INFO, "Configuration file is: " + path);
        File configurationFile = new File(path);

        if (!configurationFile.exists())
        {
            this.instance.log(Level.SEVERE, "Configuration file don't exist!");
            System.exit(4);
        }

        InputStreamReader reader = new InputStreamReader(new FileInputStream(configurationFile), "UTF-8");
        try
        {
            this.jsonConfiguration = new JsonParser().parse(reader).getAsJsonObject();
        } finally
        {
            try
            {
                reader.close();
            } catch (IOException e)
            {

            }

        }

        if (!validateJson(jsonConfiguration))
        {
            this.instance.log(Level.SEVERE, "Configuration file isn't valid! Please just modify the default configuration file!");
            System.exit(5);
        }

        this.redisIp = jsonConfiguration.get("redis-ip").getAsString();
        this.redisPort = jsonConfiguration.get("redis-port").getAsInt();
        this.redisPassword = jsonConfiguration.get("redis-password").getAsString();
        this.sqlURL = jsonConfiguration.get("sql-url").getAsString();
        this.sqlUser = jsonConfiguration.get("sql-user").getAsString();
        this.sqlPassword = jsonConfiguration.get("sql-password").getAsString();
    }

    public void createDefaultConfiguration()
    {
        try
        {
            File destinationFile = new File(MiscUtils.getJarFolder(), "config.json");
            FileUtils.copyURLToFile(Configuration.class.getResource("/config.json"), destinationFile);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        this.instance.log(Level.INFO, "Default configuration file created.");
        System.exit(3);
    }

    public JsonObject getJsonConfiguration()
    {
        return this.jsonConfiguration;
    }

    public boolean validateJson(JsonObject object)
    {
        boolean flag = true;

        /** Common **/
        if (!object.has("redis-ip")) flag = false;
        if (!object.has("redis-port")) flag = false;
        if (!object.has("redis-password")) flag = false;
        if (!object.has("sql-url")) flag = false;
        if (!object.has("sql-user")) flag = false;
        if (!object.has("sql-password")) flag = false;
        if (!object.has("web-domain")) flag = false;

        /** Client **/
        if (!object.has("max-weight")) flag = false;

        return flag;
    }
}
