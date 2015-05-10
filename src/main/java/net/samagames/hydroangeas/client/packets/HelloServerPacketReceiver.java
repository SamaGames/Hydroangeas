package net.samagames.hydroangeas.client.packets;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.packets.PacketReceiver;

public class HelloServerPacketReceiver implements PacketReceiver
{
    @Override
    public void receive(String data)
    {
        if(data.equals("hello"))
            Hydroangeas.getInstance().getAsClient().getLifeThread().connectedToServer();
    }
}
