package net.samagames.hydroangeas.server.tasks;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.protocol.intranet.HeartbeatPacket;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

import java.util.concurrent.TimeUnit;
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
public class KeepUpdatedThread
{
    private final static long TIMEOUT = 20 * 1000L;
    private final HydroangeasServer instance;

    public KeepUpdatedThread(HydroangeasServer instance)
    {
        this.instance = instance;
    }

    public void start()
    {
        instance.getScheduler().scheduleAtFixedRate(this::check, 2, 10, TimeUnit.SECONDS);
    }

    public void check()
    {
        this.instance.getClientManager().getClients().stream().forEachOrdered(client -> {
            try
            {
                instance.getConnectionManager().sendPacket(client, new HeartbeatPacket(instance.getServerUUID()));
                if (System.currentTimeMillis() - client.getTimestamp() > TIMEOUT)
                {
                    Hydroangeas.getInstance().log(Level.WARNING, "Lost connection with client " + client.getUUID() + "!");
                    //ModMessage.sendMessage(InstanceType.SERVER, "Connexion perdue avec le client " + client.getUUID() + " !");

                    instance.getClientManager().onClientNoReachable(client.getUUID());
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }
}
