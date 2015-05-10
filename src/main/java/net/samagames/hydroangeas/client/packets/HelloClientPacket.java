package net.samagames.hydroangeas.client.packets;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.common.ClientInfos;
import net.samagames.hydroangeas.common.packets.AbstractPacket;

public class HelloClientPacket extends AbstractPacket
{
    private final ClientInfos clientInfos;

    public HelloClientPacket(HydroangeasClient instance)
    {
        this.clientInfos = new ClientInfos(instance);
    }

    @Override
    public String getChannel()
    {
        return "hello@hydroangeas-server";
    }

    public ClientInfos getClientInfos()
    {
        return this.clientInfos;
    }
}
