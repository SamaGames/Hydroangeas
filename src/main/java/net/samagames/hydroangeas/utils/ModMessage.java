package net.samagames.hydroangeas.utils;

import com.google.gson.GsonBuilder;
import net.samagames.hydroangeas.Hydroangeas;
import redis.clients.jedis.Jedis;

public class ModMessage
{
    public static void sendModMessage(JsonModMessage message)
    {
        Jedis jedis = Hydroangeas.getInstance().getDatabaseConnector().getJedisPool().getResource();
        jedis.publish("moderationchan", new GsonBuilder().serializeNulls().create().toJson(message));
        jedis.close();
    }

    public static void sendModMessage(String from, ChatColor colorCode, String message)
    {
        sendModMessage(new JsonModMessage(from, colorCode, message));
    }

    public static void sendModMessageError(String from, ChatColor colorCode, String message)
    {
        sendModMessage(new JsonModMessage(from, colorCode, ChatColor.RED + "✖" + ChatColor.RESET + " " + message));
    }
}
