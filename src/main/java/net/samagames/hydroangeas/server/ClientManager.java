package net.samagames.hydroangeas.server;

import net.samagames.hydroangeas.common.protocol.CoupaingServerPacket;
import net.samagames.hydroangeas.common.protocol.HelloFromClientPacket;
import net.samagames.hydroangeas.server.client.HydroClient;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
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

    public void orderServerForCoupaing(CoupaingServerPacket packet)
    {
        //TODO select a client and send the order
    }

    public void updateClient(HelloFromClientPacket packet)
    {
        HydroClient client = this.getClientByUUID(packet.getUUID());
        if(client == null)
        {
            client = new HydroClient(instance, packet.getUUID());
            this.instance.log(Level.INFO, "New client " + client.getUUID().toString() + " connected!");
            clients.add(client);
        }
        client.updateData(packet);
    }

    public void onClientHeartbeat(UUID uuid)
    {
        HydroClient client = this.getClientByUUID(uuid);
        if(client == null)
        {
            this.instance.log(Level.INFO, "Client " + uuid.toString() + " connected!");
            //Todo ask for get data
            return;
        }

        client.setTimestamp(new Timestamp(System.currentTimeMillis()));
    }

    public void onClientNoReachable(UUID clientUUID)
    {
        HydroClient client = this.getClientByUUID(clientUUID);
        if(client != null)
            this.clients.remove(client);
    }

    public KeepUpdatedThread getKeepUpdatedThread()
    {
        return this.keepUpdatedThread;
    }

    public List<HydroClient> getClients()
    {
        List<HydroClient> data = new ArrayList<>();
        data.addAll(clients);
        return data;
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

    public MinecraftServerS getServerByName(String name)
    {
        for(HydroClient client : clients)
        {
            MinecraftServerS server = client.getServerManager().getServerByName(name);
            if(server != null)
            {
                return server;
            }
        }
        return null;
    }
}
