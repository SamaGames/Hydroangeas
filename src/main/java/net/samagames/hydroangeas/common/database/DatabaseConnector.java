package net.samagames.hydroangeas.common.database;

import net.samagames.hydroangeas.Hydroangeas;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnector
{
    private final Hydroangeas instance;
    public Thread reconnection;
    private JedisPool jedisPool;

    public DatabaseConnector(Hydroangeas instance)
    {
        this.instance = instance;
        this.connect();

        this.reconnection = new Thread(() -> {
            while (true)
            {
                try
                {
                    try
                    {
                        jedisPool.getResource().close();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        instance.getLogger().severe("Error redis connection, Try to reconnect!");
                        connect();
                    }
                    Thread.sleep(10 * 1000);
                } catch (Exception e)
                {
                    break;
                }
            }
        }, "Redis reconnect");
        reconnection.start();
    }

    public void connect()
    {
        this.instance.log(Level.INFO, "Connecting to database...");

        JedisPoolConfig jedisConfiguration = new JedisPoolConfig();
        jedisConfiguration.setMaxTotal(-1);
        jedisConfiguration.setJmxEnabled(false);
        jedisConfiguration.setMaxWaitMillis(5000);

        Logger logger = Logger.getLogger(JedisPool.class.getName());
        logger.setLevel(Level.OFF);

        this.jedisPool = new JedisPool(jedisConfiguration, this.instance.getConfiguration().redisIp, this.instance.getConfiguration().redisPort, 5000, this.instance.getConfiguration().redisPassword);
        try
        {
            this.jedisPool.getResource().close();
        } catch (Exception e)
        {
            this.instance.log(Level.SEVERE, "Can't connect to the database!");
            System.exit(8);
        }

        this.instance.log(Level.INFO, "Connected to database.");
    }

    public void disconnect()
    {
        reconnection.interrupt();
    }

    public JedisPool getJedisPool()
    {
        return this.jedisPool;
    }
}
