package net.samagames.hydroangeas.server.client;

import net.samagames.hydroangeas.common.protocol.coupaings.CoupaingServerPacket;
import net.samagames.hydroangeas.common.protocol.intranet.AskForClientDataPacket;
import net.samagames.hydroangeas.common.protocol.intranet.HelloFromClientPacket;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.games.AbstractGameTemplate;
import net.samagames.hydroangeas.server.games.SimpleGameTemplate;
import net.samagames.hydroangeas.server.tasks.KeepUpdatedThread;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class ClientManager
{
    private final HydroangeasServer instance;
    private final KeepUpdatedThread keepUpdatedThread;
    private CopyOnWriteArrayList<HydroClient> clientList = new CopyOnWriteArrayList<>();

    public ClientManager(HydroangeasServer instance)
    {
        this.instance = instance;

        this.keepUpdatedThread = new KeepUpdatedThread(instance);
        this.keepUpdatedThread.start();
    }

    public void orderServerForCoupaing(CoupaingServerPacket packet)
    {
        SimpleGameTemplate template = new SimpleGameTemplate(
                UUID.randomUUID().toString(),
                packet.getGame(),
                packet.getMap(),
                packet.getMinSlot(),
                packet.getMaxSlot(),
                packet.getOptions(),
                packet.getWeight(),
                true);

        instance.getAlgorithmicMachine().orderTemplate(template);
    }

    public void updateClient(HelloFromClientPacket packet)
    {
        HydroClient client = this.getClientByUUID(packet.getUUID());
        if (client == null)
        {
            client = new HydroClient(instance, packet.getUUID());
            this.instance.log(Level.INFO, "New client " + client.getUUID() + " connected!");
            if (!clientList.add(client)) instance.log(Level.INFO, "Not added !");
        }
        client.updateData(packet);
    }

    public void onClientHeartbeat(UUID uuid)
    {
        HydroClient client = this.getClientByUUID(uuid);
        if (client == null)
        {
            this.instance.log(Level.INFO, "Client " + uuid + " connected!");
            instance.getConnectionManager().sendPacket(uuid, new AskForClientDataPacket());
            return;
        }

        client.setTimestamp(System.currentTimeMillis());
    }

    public void onClientNoReachable(UUID clientUUID)
    {
        HydroClient client = this.getClientByUUID(clientUUID);
        for (MinecraftServerS serverS : client.getServerManager().getServers())
        {
            if (serverS.isHub())
            {
                instance.getHubBalancer().onHubShutdown(serverS);
            }
            client.getServerManager().removeServer(serverS.getServerName());
        }
        if (!clientList.remove(client)) instance.log(Level.INFO, "Not deleted !");
    }

    public KeepUpdatedThread getKeepUpdatedThread()
    {
        return this.keepUpdatedThread;
    }

    public List<HydroClient> getClients()
    {
        return clientList;
    }

    public HydroClient getClientByUUID(UUID uuid)
    {
        if (uuid == null)
            return null;

        for (HydroClient client : clientList)
        {
            if (client.getUUID().equals(uuid))
            {
                return client;
            }
        }
        return null;
    }

    public MinecraftServerS getServerByName(String name)
    {

        for (HydroClient client : clientList)
        {
            MinecraftServerS server = client.getServerManager().getServerByName(name);
            if (server != null)
            {
                return server;
            }
        }
        return null;
    }

    public List<MinecraftServerS> getServersByTemplate(AbstractGameTemplate template)
    {
        List<MinecraftServerS> servers = new ArrayList<>();
        for (HydroClient client : clientList)
        {
            servers.addAll(client.getServerManager().getServersByTemplate(template));
        }

        return servers;
    }
}
