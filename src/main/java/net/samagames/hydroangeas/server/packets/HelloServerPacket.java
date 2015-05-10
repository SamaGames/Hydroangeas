package net.samagames.hydroangeas.server.packets;

import net.samagames.hydroangeas.client.packets.HelloClientPacket;
import net.samagames.hydroangeas.common.packets.AbstractPacket;

public class HelloServerPacket extends AbstractPacket
{
    private final HelloClientPacket clientPacket;

    public HelloServerPacket(HelloClientPacket clientPacket)
    {
        this.clientPacket = clientPacket;
    }

    @Override
    public String getChannel()
    {
        return "hello@" + this.clientPacket.getClientInfos().getClientUUID().toString() + "@hydroangeas-client";
    }

    @Override
    public String getData()
    {
        return "hello";
    }
}
