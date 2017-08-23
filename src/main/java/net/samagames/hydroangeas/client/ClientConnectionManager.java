package net.samagames.hydroangeas.client;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;
import net.samagames.hydroangeas.common.packets.AbstractPacket;
import net.samagames.hydroangeas.common.packets.ConnectionManager;
import net.samagames.hydroangeas.common.protocol.intranet.*;

import java.util.concurrent.TimeUnit;

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
public class ClientConnectionManager extends ConnectionManager
{

    public HydroangeasClient instance;

    public ClientConnectionManager(Hydroangeas hydroangeas)
    {
        super(hydroangeas);

        instance = hydroangeas.getAsClient();
    }

    public void sendPacket(AbstractPacket packet)
    {
        String channel = "global@hydroangeas-server";
        sendPacket(channel, packet);
    }

    @Override
    public void handler(int id, String data)
    {
        Object spacket = gson.fromJson(data, packets[id].getClass());

        if (spacket instanceof HeartbeatPacket)
        {
            HeartbeatPacket heartbeatPacket = (HeartbeatPacket) spacket;
            instance.getLifeThread().onServerHeartbeat(heartbeatPacket.getUUID());
        } else if (spacket instanceof MinecraftServerSyncPacket)
        {
            MinecraftServerSyncPacket packet = (MinecraftServerSyncPacket) spacket;

            Hydroangeas.getInstance().getAsClient().getServerManager().newServer(packet);
        }else if (spacket instanceof AskForClientDataPacket)
        {
            AskForClientDataPacket packet = (AskForClientDataPacket) spacket;
            instance.getScheduler().schedule(() -> {
                try {
                    Hydroangeas.getInstance().getAsClient().getLifeThread().sendData(true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, 6, TimeUnit.SECONDS);
        } else if (spacket instanceof AskForClientActionPacket)
        {
            AskForClientActionPacket packet = (AskForClientActionPacket) spacket;

            switch (packet.getCommand())
            {
                case SERVEREND:
                    MinecraftServerC server = instance.getServerManager().getServerByName(packet.getData());
                    if(server != null)
                    {
                        server.stopServer();
                    }
                    break;
                case CLIENTSHUTDOWN:
                    System.exit(0);
                    break;
                case CONSOLECOMMAND:
                    instance.getCommandManager().inputCommand(packet.getData());
                    break;
            }
        }
    }
}
