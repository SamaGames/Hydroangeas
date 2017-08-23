package net.samagames.hydroangeas.server.receiver;

import com.google.gson.Gson;
import net.samagames.hydroangeas.common.packets.PacketReceiver;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.server.data.ServerStatus;
import net.samagames.hydroangeas.server.data.Status;
import net.samagames.hydroangeas.server.waitingqueue.Queue;

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
public class ServerStatusReceiver implements PacketReceiver
{

    public HydroangeasServer instance;

    public ServerStatusReceiver(HydroangeasServer instance)
    {
        this.instance = instance;
    }

    @Override
    public void receive(String packet)
    {
        ServerStatus data = new Gson().fromJson(packet, ServerStatus.class);

        String serverName = data.getBungeeName();

        MinecraftServerS server = instance.getClientManager().getServerByName(serverName);

        if (server == null)
        {
            instance.getLogger().info("Server: " + serverName + " not handled by Hydro");
            instance.getLogger().info("Fetching all clients!");
            instance.getClientManager().globalCheckData();
            return;
        }

        server.setActualSlots(data.getPlayers());
        if(data.getStatus() == null)
        {
            instance.getLogger().info("Server: " + serverName + " has a null status.");
        }
        server.setStatus(data.getStatus());

        Queue queue = instance.getQueueManager().getQueueByTemplate(server.getTemplateID());
        if (queue != null)
        {
            queue.updateInfosToHub();
        }
    }
}
