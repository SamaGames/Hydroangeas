package net.samagames.hydroangeas.server;

import net.samagames.hydroangeas.common.protocol.HelloFromClientPacket;
import net.samagames.hydroangeas.server.client.HydroClient;
import net.samagames.hydroangeas.server.scheduler.KeepUpdatedThread;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class ClientManager
{
    private final HydroangeasServer instance;
    private final List<HydroClient> clients = new ArrayList<>();
    //private final HashMap<UUID, ClientInfos> clients = new HashMap<>();
    private final KeepUpdatedThread keepUpdatedThread;

    public ClientManager(HydroangeasServer instance)
    {
        this.instance = instance;

        this.keepUpdatedThread = new KeepUpdatedThread(instance);
        this.keepUpdatedThread.start();
    }

    public void updateClient(HelloFromClientPacket packet)
    {
        HydroClient client = this.getClientByUUID(packet.getUUID());
        if(client == null)
        {

        }
    }

    public void onClientHeartbeat(UUID uuid)
    {
        HydroClient client = this.getClientByUUID(uuid);
        if(client == null)
        {
            this.instance.log(Level.INFO, "Client " + uuid.toString() + " connected!");
            //Todo get data
            return;
        }

        client.setTimestamp(new Timestamp(System.currentTimeMillis()));
    }

    public void onClientNoReachable(UUID clientUUID)
    {
        if(this.getClientByUUID(clientUUID) == null)
            this.clients.remove(clientUUID);
    }

    public KeepUpdatedThread getKeepUpdatedThread()
    {
        return this.keepUpdatedThread;
    }

    public List<HydroClient> getClients()
    {
        return this.clients;
    }

    public HydroClient getClientByUUID(UUID uuid)
    {
        for(HydroClient client : clients)
        {
            if(client.getUUID().equals(uuid)){
                return client;
            }
        }
        return null;
    }
}
