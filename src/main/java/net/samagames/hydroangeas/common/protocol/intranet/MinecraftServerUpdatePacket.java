package net.samagames.hydroangeas.common.protocol.intranet;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.common.packets.AbstractPacket;

import java.util.UUID;

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
public class MinecraftServerUpdatePacket extends AbstractPacket
{

    private UType action;

    private UUID uuid;
    private String serverName;

    private int newWeight;
    private int maxWeight;


    public MinecraftServerUpdatePacket(HydroangeasClient instance, String serverName, UType action)
    {
        this.uuid = instance.getClientUUID();
        this.newWeight = instance.getActualWeight();
        this.maxWeight = instance.getMaxWeight();

        this.serverName = serverName;
        this.action = action;
    }

    public MinecraftServerUpdatePacket()
    {

    }

    public UUID getUUID()
    {
        return uuid;
    }

    public String getServerName()
    {
        return serverName;
    }

    public UType getAction()
    {
        return action;
    }

    public int getNewWeight()
    {
        return newWeight;
    }

    public int getMaxWeight()
    {
        return maxWeight;
    }

    public enum UType
    {
        START, END
    }
}
