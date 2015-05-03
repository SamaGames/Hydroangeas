package net.samagames.hydroangeas.common.database;

import net.samagames.hydroangeas.Hydroangeas;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnector
{
    private final Hydroangeas instance;
    private JedisPool jedisPool;

    public DatabaseConnector(Hydroangeas instance)
    {
        this.instance = instance;
        this.connect();
    }

    public void connect()
    {
        this.instance.log(Level.INFO, "Connecting to database...");

        JedisPoolConfig jedisConfiguration = new JedisPoolConfig();
        jedisConfiguration.setMaxTotal(1024);
        jedisConfiguration.setMaxWaitMillis(5000);

        Logger logger = Logger.getLogger(JedisPool.class.getName());
        logger.setLevel(Level.OFF);

        this.jedisPool = new JedisPool(jedisConfiguration, this.instance.getConfiguration().redisIp, this.instance.getConfiguration().redisPort, 5000, this.instance.getConfiguration().redisPassword);

        try
        {
            this.jedisPool.getResource();
        }
        catch (Exception e)
        {
            this.instance.log(Level.SEVERE, "Can't connect to the database!");
            System.exit(-1);
        }

        this.instance.log(Level.INFO, "Connected to database.");
    }

    public JedisPool getJedisPool()
    {
        return this.jedisPool;
    }
}
