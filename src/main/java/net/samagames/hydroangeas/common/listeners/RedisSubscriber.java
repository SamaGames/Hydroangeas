package net.samagames.hydroangeas.common.listeners;

import net.samagames.hydroangeas.Hydroangeas;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

public class RedisSubscriber extends JedisPubSub
{
    private final Hydroangeas instance;
    private final HashMap<String, HashSet<PacketReceiver>> packetsReceivers;

    public RedisSubscriber(Hydroangeas instance)
    {
        this.instance = instance;
        this.packetsReceivers = new HashMap<>();
    }

    @Override
    public void onMessage(String channel, String message)
    {
        try
        {
            HashSet<PacketReceiver> receivers = this.packetsReceivers.get(channel);

            if (receivers != null)
                receivers.forEach((PacketReceiver receiver) -> receiver.receive(channel, message));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

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
}
