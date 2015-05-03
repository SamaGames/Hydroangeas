package net.samagames.hydroangeas;

import joptsimple.OptionSet;
import net.samagames.hydroangeas.common.database.DatabaseConnector;
import net.samagames.hydroangeas.common.database.RedisSubscriber;

import java.util.logging.Level;

public abstract class Hydroangeas
{
    private static Hydroangeas instance;

    protected Configuration configuration;
    protected DatabaseConnector databaseConnector;
    protected RedisSubscriber redisSubscriber;

    public Hydroangeas(OptionSet options)
    {
        instance = this;

        System.out.println("Hydroangeas version 1.0.0 by BlueSlime");
        System.out.println("----------------------------------------");

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
        String finalMessage = "[" + level.getName().toUpperCase() + "] " + message;

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

    public static Hydroangeas getInstance()
    {
        return instance;
    }
}
