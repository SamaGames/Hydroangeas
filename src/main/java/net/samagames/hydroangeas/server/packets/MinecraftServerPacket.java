package net.samagames.hydroangeas.server.packets;

import net.samagames.hydroangeas.common.informations.ClientInfos;
import net.samagames.hydroangeas.common.informations.MinecraftServerInfos;
import net.samagames.hydroangeas.common.packets.AbstractPacket;

public class MinecraftServerPacket extends AbstractPacket
{
    private ClientInfos clientInfos;
    private MinecraftServerInfos serverInfos;

    public MinecraftServerPacket(ClientInfos clientInfos, MinecraftServerInfos serverInfos)
    {
        this.clientInfos = clientInfos;
        this.serverInfos = serverInfos;
    }

    public ClientInfos getClientInfos()
    {
        return this.clientInfos;
    }

    public MinecraftServerInfos getServerInfos()
    {
        return this.serverInfos;
    }

    @Override
    public String getChannel()
    {
        return "server@" + clientInfos.getClientName() + "@hydroangeas-client";
    }
}
