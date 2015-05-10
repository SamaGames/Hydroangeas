package net.samagames.hydroangeas.server;

import net.samagames.hydroangeas.client.packets.HelloClientPacket;
import net.samagames.hydroangeas.common.ClientInfos;
import net.samagames.hydroangeas.server.packets.HelloServerPacket;
import net.samagames.hydroangeas.server.scheduler.KeepUpdatedThread;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class ClientManager
{
    private final HydroangeasServer instance;
    private final HashMap<UUID, ClientInfos> clients;
    private final KeepUpdatedThread keepUpdatedThread;

    public ClientManager(HydroangeasServer instance)
    {
        this.instance = instance;
        this.clients = new HashMap<>();

        this.keepUpdatedThread = new KeepUpdatedThread(instance);
        this.keepUpdatedThread.start();
    }

    public void onClientHeartbeat(HelloClientPacket packet)
    {
        if(!this.clients.containsKey(packet.getClientInfos().getClientUUID()))
        {
            String dedicatedGame = (packet.getClientInfos().getDedicatedGame() == null ? "No dedicated game" : "Dedicated game is " + packet.getClientInfos().getDedicatedGame());
            this.instance.log(Level.INFO, "Client " + packet.getClientInfos().getClientUUID().toString() + " connected! " + dedicatedGame + ".");
        }

        this.clients.put(packet.getClientInfos().getClientUUID(), packet.getClientInfos());

        new HelloServerPacket(packet).send();
    }

    public void onClientNoReachable(UUID clientUUID)
    {
        if(this.clients.containsKey(clientUUID))
            this.clients.remove(clientUUID);
    }

    public ClientInfos getClientInfosByUUID(UUID clientUUID)
    {
        if(this.clients.containsKey(clientUUID))
            return this.clients.get(clientUUID);
        else
            return null;
    }

    public KeepUpdatedThread getKeepUpdatedThread()
    {
        return this.keepUpdatedThread;
    }

    public HashMap<UUID, ClientInfos> getClients()
    {
        return this.clients;
    }
}
