package net.samagames.hydroangeas.utils;

import com.google.gson.GsonBuilder;
import net.samagames.hydroangeas.Hydroangeas;
import redis.clients.jedis.Jedis;

public class ModMessage
{
    public static void sendModMessage(JsonModMessage message)
    {
        /*Jedis jedis = Hydroangeas.getInstance().getDatabaseConnector().getJedisPool().getResource();
        jedis.publish("moderationchan", new GsonBuilder().serializeNulls().create().toJson(message));
        jedis.close();*/
    }

    public static void sendMessage(InstanceType type, String message)
    {
        sendModMessage(new JsonModMessage("Hydroangeas " + type, ChatColor.GREEN, message));
    }

    public static void sendError(InstanceType type, String message)
    {
        sendModMessage(new JsonModMessage("Hydroangeas " + type, ChatColor.GREEN, ChatColor.RED + "✖" + ChatColor.RESET + " " + message));
    }

    public static void sendDebug(String message)
    {
        sendModMessage(new JsonModMessage("Hydroangeas DEBUG", ChatColor.DARK_PURPLE, message));
    }
}
