package net.samagames.hydroangeas;

import joptsimple.OptionSet;
import net.samagames.hydroangeas.common.listeners.RedisSubscriber;

import java.util.logging.Level;

public class Hydroangeas
{
    private static Hydroangeas instance;

    protected Configuration configuration;
    protected RedisSubscriber redisSubscriber;

    private boolean debug;

    public Hydroangeas(OptionSet options)
    {
        instance = this;

        System.out.println("Hydroangeas version 1.0.0 by BlueSlime");
        System.out.println("----------------------------------------");

        this.debug = options.has("debug");
        this.configuration = new Configuration(this, options);
        this.redisSubscriber = new RedisSubscriber(this);
    }

    public void log(Level level, String message)
    {
        String finalMessage = "[" + level.getName().toUpperCase() + "] " + message;

        if(level == Level.SEVERE)
            System.err.println(finalMessage);
        else
            System.out.println(finalMessage);
    }

    public boolean isDebugEnabled()
    {
        return this.debug;
    }

    public static Hydroangeas getInstance()
    {
        return instance;
    }
}
