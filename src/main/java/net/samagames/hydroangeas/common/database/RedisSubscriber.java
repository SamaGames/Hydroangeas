package net.samagames.hydroangeas.common.database;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.packets.PacketReceiver;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.HashSet;
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
public class RedisSubscriber extends JedisPubSub
{
    private final Hydroangeas instance;
    private final HashMap<String, HashSet<PacketReceiver>> packetsReceivers;
    private boolean continueLoop;

    public RedisSubscriber(Hydroangeas instance)
    {
        this.instance = instance;
        this.packetsReceivers = new HashMap<>();
        this.continueLoop = true;

        new Thread(() ->
        {
            while (this.continueLoop)
            {
                Jedis jedis = this.instance.getDatabaseConnector().getJedisPool().getResource();
                try
                {
                    jedis.psubscribe(this, "*");
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                this.instance.log(Level.INFO, "Disconnected from database.");
                jedis.close();
            }
        }).start();

        this.instance.log(Level.INFO, "Subscribing PubSub...");

        while (!this.isSubscribed())
        {
            try
            {
                Thread.sleep(100);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        this.instance.log(Level.INFO, "PubSub subscribed.");
    }

    public void disable()
    {
        this.continueLoop = false;
        this.unsubscribe();
        this.punsubscribe();
    }

    public void registerReceiver(String channel, PacketReceiver receiver)
    {
        HashSet<PacketReceiver> receivers = this.packetsReceivers.get(channel);

        if (receivers == null)
            receivers = new HashSet<>();

        receivers.add(receiver);
        this.subscribe(channel);
        this.packetsReceivers.put(channel, receivers);

        this.instance.log(Level.INFO, "Registered receiver '" + receiver.getClass().getSimpleName() + "' on channel '" + channel + "'");
    }

    public void send(String channel, String packet)
    {
        Jedis jedis = this.instance.getDatabaseConnector().getJedisPool().getResource();
        jedis.publish(channel, packet);
        jedis.close();
    }

    @Override
    public void onMessage(String channel, String message)
    {
        HashSet<PacketReceiver> receivers = this.packetsReceivers.get(channel);

        if (receivers != null)
            receivers.forEach((PacketReceiver receiver) -> receiver.receive(message));
    }
}
