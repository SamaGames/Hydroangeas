package net.samagames.hydroangeas.utils;

import com.google.gson.GsonBuilder;
import net.samagames.hydroangeas.Hydroangeas;
import redis.clients.jedis.Jedis;

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
