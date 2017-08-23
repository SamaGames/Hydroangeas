package net.samagames.hydroangeas.server.client;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.protocol.intranet.AskForClientActionPacket;
import net.samagames.hydroangeas.common.protocol.intranet.HelloFromClientPacket;
import net.samagames.hydroangeas.server.HydroangeasServer;

import java.util.List;
import java.util.UUID;
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
public class HydroClient
{

    private HydroangeasServer instance;
    private UUID uuid;
    private String ip;
    private int maxWeight;
    private long timestamp;

    private Hydroangeas.RestrictionMode restrictionMode;
    private List<String> whitelist;
    private List<String> blacklist;

    private MinecraftServerManager serverManager;

    public HydroClient(HydroangeasServer instance, UUID uuid, Hydroangeas.RestrictionMode restrictionMode, List<String> whitelist, List<String> blacklist)
    {
        this.instance = instance;
        this.uuid = uuid;
        this.restrictionMode = restrictionMode;
        this.whitelist = whitelist;
        this.blacklist = blacklist;

        this.timestamp = System.currentTimeMillis();

        serverManager = new MinecraftServerManager(instance, this);
    }

    public void updateData(HelloFromClientPacket packet)
    {
        if (uuid == null)
            this.uuid = packet.getUUID();

        setIp(packet.getIp());

        setMaxWeight(packet.getMaxWeight());

        if (getActualWeight() != packet.getActualWeight())
        {
            instance.log(Level.SEVERE, "Error client and server not sync about weight! client:" + packet.getActualWeight() + " server:" + getActualWeight());
        }

        this.timestamp = System.currentTimeMillis();

    }

    public void shutdown()
    {
        instance.getConnectionManager().sendPacket(this, new AskForClientActionPacket(instance.getUUID(), AskForClientActionPacket.ActionCommand.CLIENTSHUTDOWN, ""));
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public String getIp()
    {
        return this.ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public int getMaxWeight()
    {
        return this.maxWeight;
    }

    public void setMaxWeight(int maxWeight)
    {
        this.maxWeight = maxWeight;
    }

    public int getActualWeight()
    {
        return serverManager.getTotalWeight();
    }

    public int getAvailableWeight()
    {
        return getMaxWeight() - getActualWeight();
    }

    public long getTimestamp()
    {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public MinecraftServerManager getServerManager()
    {
        return this.serverManager;
    }

    public HydroangeasServer getInstance()
    {
        return instance;
    }

    public int getPlayer()
    {
        int data = 0;
        for(MinecraftServerS server : serverManager.getServers())
        {
            data += server.getActualSlots();
        }
        return data;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof HydroClient)
        {
            if (this.getUUID().equals(((HydroClient) obj).getUUID()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.getUUID().hashCode();
    }

    public List<String> getWhitelist() {
        return whitelist;
    }

    public List<String> getBlacklist() {
        return blacklist;
    }

    public Hydroangeas.RestrictionMode getRestrictionMode() {
        return restrictionMode;
    }
}
