package net.samagames.hydroangeas.server.packets;

import com.google.gson.Gson;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.packets.MinecraftServerIssuePacket;
import net.samagames.hydroangeas.common.packets.PacketReceiver;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

import java.util.logging.Level;

public class MinecraftServerIssueReceiver implements PacketReceiver
{
    @Override
    public void receive(String data)
    {
        MinecraftServerIssuePacket packet = new Gson().fromJson(data, MinecraftServerIssuePacket.class);

        Hydroangeas.getInstance().log(Level.SEVERE, "An error occurred with the client '" + packet.getClientInfos().getClientUUID().toString() + "'!");
        Hydroangeas.getInstance().log(Level.SEVERE, "> Category: Server issue (" + packet.getIssueType().name() + ")");

        ModMessage.sendError(InstanceType.SERVER, packet.getMessage());
    }
}
