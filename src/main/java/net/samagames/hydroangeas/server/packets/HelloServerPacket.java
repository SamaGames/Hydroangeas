package net.samagames.hydroangeas.server.packets;

import net.samagames.hydroangeas.common.packets.AbstractPacket;

public class HelloServerPacket extends AbstractPacket
{
    @Override
    public String getChannel()
    {
        return "hello@hydroangeas-client";
    }

    @Override
    public String getData()
    {
        return "hello";
    }
}
