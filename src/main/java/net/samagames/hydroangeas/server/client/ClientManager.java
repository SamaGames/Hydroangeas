package net.samagames.hydroangeas.server.client;

import net.samagames.hydroangeas.common.protocol.coupaings.CoupaingServerPacket;
import net.samagames.hydroangeas.common.protocol.intranet.AskForClientDataPacket;
import net.samagames.hydroangeas.common.protocol.intranet.HelloFromClientPacket;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.games.AbstractGameTemplate;
import net.samagames.hydroangeas.server.games.SimpleGameTemplate;
import net.samagames.hydroangeas.server.tasks.CleanServer;
import net.samagames.hydroangeas.server.tasks.KeepUpdatedThread;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

/*
 * This file is part of Hydroangeas.
 *
 * Hydroangeas is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hydroangeas is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hydroangeas.  If not, see <http://www.gnu.org/licenses/>.
 */
public class ClientManager
{
    private final HydroangeasServer instance;
    private final KeepUpdatedThread keepUpdatedThread;
    private final CleanServer cleanServer;
    private CopyOnWriteArrayList<HydroClient> clientList = new CopyOnWriteArrayList<>();

    private ScheduledExecutorService executorService;

    public ClientManager(HydroangeasServer instance)
    {
        this.instance = instance;

        this.executorService = Executors.newScheduledThreadPool(1);

        this.keepUpdatedThread = new KeepUpdatedThread(instance);
        this.keepUpdatedThread.start();

        this.cleanServer = new CleanServer(instance);
        this.cleanServer.start();
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
        executorService.execute(() -> {
            HydroClient client = getClientByUUID(packet.getUUID());
            if (client == null) {
                client = new HydroClient(instance, packet.getUUID(), packet.getRestrictionMode(), packet.getWhitelist(), packet.getBlacklist());
                instance.log(Level.INFO, "New client " + client.getUUID() + " connected!");
                if (!clientList.add(client)) instance.log(Level.INFO, "Not added !");
            }
            client.updateData(packet);
        });
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
        executorService.execute(() -> {
            HydroClient client = getClientByUUID(clientUUID);

            if (client.getUUID().equals(clientUUID)) {
                client.getServerManager().getServers().stream().filter(serverS -> serverS.isHub()).forEach(serverS -> {
                    instance.getHubBalancer().onHubShutdown(serverS);
                });
                if (!clientList.remove(client)) instance.log(Level.INFO, "Not deleted !");
            }
        });
    }

    public void globalCheckData()
    {
        instance.getConnectionManager().sendPacket("globalSecurity@hydroangeas-client", new AskForClientDataPacket(instance.getServerUUID()));
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

    public List<MinecraftServerS> getServersStartingBy(String regex)
    {
        List<MinecraftServerS> servers = new ArrayList<>();
        for (HydroClient client : clientList)
        {
            for(MinecraftServerS server : client.getServerManager().getServers())
            {
                if(server.getServerName().startsWith(regex))
                {
                    servers.add(server);
                }
            }
        }
        return servers;
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
