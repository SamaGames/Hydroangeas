package net.samagames.hydroangeas.client.packets;

import net.samagames.hydroangeas.client.servers.MinecraftServer;
import net.samagames.hydroangeas.common.informations.MinecraftServerInfos;
import net.samagames.hydroangeas.common.packets.AbstractPacket;

public class MinecraftServerEndPacket extends AbstractPacket
{
    private final MinecraftServerInfos serverInfos;

    public MinecraftServerEndPacket(MinecraftServer server)
    {
        this.serverInfos = server.getServerInfos();
    }

    public MinecraftServerInfos getServerInfos()
    {
        return this.serverInfos;
    }

    @Override
    public String getChannel()
    {
        return "end@hydroangeas-server";
    }
}
