package net.samagames.hydroangeas.client.packets;

import com.google.gson.Gson;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.packets.PacketReceiver;
import net.samagames.hydroangeas.server.packets.MinecraftServerPacket;

public class MinecraftServerReceiver implements PacketReceiver
{
    @Override
    public void receive(String data)
    {
        MinecraftServerPacket packet = new Gson().fromJson(data, MinecraftServerPacket.class);
        Hydroangeas.getInstance().getAsClient().getServerManager().newServer(packet.getServerInfos());
    }
}
