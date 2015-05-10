package net.samagames.hydroangeas.server;

import net.samagames.hydroangeas.client.packets.HelloClientPacket;
import net.samagames.hydroangeas.common.ClientInfos;
import net.samagames.hydroangeas.server.packets.HelloServerPacket;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class ClientManager
{
    private final HydroangeasServer instance;
    private final HashMap<UUID, ClientInfos> clients;

    public ClientManager(HydroangeasServer instance)
    {
        this.instance = instance;
        this.clients = new HashMap<>();
    }

    public void onClientHeartbeat(HelloClientPacket packet)
    {
        if(this.clients.containsKey(packet.getClientInfos().getClientUUID()))
        {
            this.instance.log(Level.INFO, "Client " + packet.getClientInfos().getClientUUID().toString() + " sent an heartbeat!");
        }
        else
        {
            String dedicatedGame = (packet.getClientInfos().getDedicatedGame() == null ? "No dedicated game" : "Dedicated game is " + packet.getClientInfos().getDedicatedGame());
            this.instance.log(Level.INFO, "Client " + packet.getClientInfos().getClientUUID().toString() + " connected! " + dedicatedGame + ".");

            this.clients.put(packet.getClientInfos().getClientUUID(), packet.getClientInfos());
        }

        new HelloServerPacket().send();
    }

    public HashMap<UUID, ClientInfos> getClients()
    {
        return this.clients;
    }
}
