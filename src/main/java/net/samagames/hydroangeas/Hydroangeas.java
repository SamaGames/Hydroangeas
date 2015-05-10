package net.samagames.hydroangeas;

import joptsimple.OptionSet;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.common.database.DatabaseConnector;
import net.samagames.hydroangeas.common.database.RedisSubscriber;
import net.samagames.hydroangeas.server.HydroangeasServer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public abstract class Hydroangeas
{
    private static Hydroangeas instance;

    protected OptionSet options;
    protected Configuration configuration;
    protected DatabaseConnector databaseConnector;
    protected RedisSubscriber redisSubscriber;

    public Hydroangeas(OptionSet options)
    {
        instance = this;

        System.out.println("Hydroangeas version 1.0.0 by BlueSlime");
        System.out.println("----------------------------------------");

        this.options = options;
        this.configuration = new Configuration(this, options);
        this.databaseConnector = new DatabaseConnector(this);
        this.redisSubscriber = new RedisSubscriber(this);

        this.enable();

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            this.log(Level.INFO, "Shutdown asked!");
            this.shutdown();
            this.log(Level.INFO, "Bye!");
        }));
    }

    public abstract void enable();

    public void shutdown()
    {
        this.redisSubscriber.disable();
    }

    public void log(Level level, String message)
    {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String finalMessage = "[" + dateFormat.format(new Date()) + "]" + " [" + level.getName().toUpperCase() + "] " + message;

        if(level == Level.SEVERE)
            System.err.println(finalMessage);
        else
            System.out.println(finalMessage);
    }

    public Configuration getConfiguration()
    {
        return this.configuration;
    }

    public DatabaseConnector getDatabaseConnector()
    {
        return this.databaseConnector;
    }

    public RedisSubscriber getRedisSubscriber()
    {
        return this.redisSubscriber;
    }

    public HydroangeasClient getAsClient()
    {
        if(this instanceof HydroangeasClient)
            return (HydroangeasClient) this;
        else
            return null;
    }

    public HydroangeasServer getAsServer()
    {
        if(this instanceof HydroangeasServer)
            return (HydroangeasServer) this;
        else
            return null;
    }

    public static Hydroangeas getInstance()
    {
        return instance;
    }
}
