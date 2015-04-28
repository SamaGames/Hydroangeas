package net.samagames.hydroangeas.common.listeners;

public interface PacketReceiver
{
    void receive(String channel, String packet);
}
