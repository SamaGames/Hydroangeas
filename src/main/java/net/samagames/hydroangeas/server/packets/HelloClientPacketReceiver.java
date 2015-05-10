package net.samagames.hydroangeas.server.packets;

import com.google.gson.Gson;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.packets.HelloClientPacket;
import net.samagames.hydroangeas.common.packets.PacketReceiver;

public class HelloClientPacketReceiver implements PacketReceiver
{
    @Override
    public void receive(String data)
    {
        HelloClientPacket packet = new Gson().fromJson(data, HelloClientPacket.class);
        Hydroangeas.getInstance().getAsServer().getClientManager().onClientHeartbeat(packet);
    }
}
