package net.samagames.hydroangeas.common.protocol.intranet;

import com.google.gson.JsonElement;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;
import net.samagames.hydroangeas.common.packets.AbstractPacket;
import net.samagames.hydroangeas.server.client.MinecraftServerS;

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
public class MinecraftServerSyncPacket extends AbstractPacket {

    private UUID clientUUID;
    private UUID minecraftUUID;

    private int port;

    private Integer hubID;
    private String game;
    private String map;
    private String templateID;

    private int minSlot;
    private int maxSlot;

    private UUID asker = null;
    private boolean isCoupaing = true;

    private JsonElement options, startupOptions;
    private int weight;

    private long timeToLive = 14400000L;
    private long startedTime;

    public MinecraftServerSyncPacket(MinecraftServerS server)
    {
        this(null,
                server.getUUID(),
                -1,
                server.getHubID(),
                server.getGame(),
                server.getMap(),
                server.getTemplateID(),
                server.getMinSlot(),
                server.getMaxSlot(),
                server.getOptions(),
                server.getStartupOptions(),
                server.getWeight(),
                server.getOwner(),
                server.isCoupaingServer());
    }

    public MinecraftServerSyncPacket(HydroangeasClient client, MinecraftServerC server)
    {
        this(client.getClientUUID(),
                server.getUUID(),
                server.getPort(),
                server.getHubID(),
                server.getGame(),
                server.getMap(),
                server.getTemplateID(),
                server.getMinSlot(),
                server.getMaxSlot(),
                server.getOptions(),
                server.getStartupOptions(),
                server.getWeight(),
                null,
                server.isCoupaingServer());
    }

    public MinecraftServerSyncPacket(UUID clientUUID,
                                     UUID minecraftUUID,
                                     int port,
                                     Integer hubID,
                                     String game,
                                     String map,
                                     String templateID,
                                     int minSlot,
                                     int maxSlot,
                                     JsonElement options,
                                     JsonElement startupOptions,
                                     int weight,
                                     UUID asker,
                                     boolean isCoupaing)
    {
        this.clientUUID = clientUUID;
        this.minecraftUUID = minecraftUUID;

        this.port = port;

        this.hubID = hubID;
        this.game = game;
        this.map = map;
        this.templateID = templateID;

        this.minSlot = minSlot;
        this.maxSlot = maxSlot;

        this.options = options;
        this.startupOptions = startupOptions;
        this.weight = weight;

        this.asker = asker;
        this.isCoupaing = isCoupaing;
    }

    public MinecraftServerSyncPacket()
    {
    }

    public String getGame()
    {
        return this.game;
    }

    public String getMap()
    {
        return this.map;
    }

    public String getServerName()
    {
        return this.game + "_" + ((hubID == null) ? this.minecraftUUID.toString().split("-")[0] : hubID);
    }

    public int getMinSlot()
    {
        return this.minSlot;
    }

    public int getMaxSlot()
    {
        return this.maxSlot;
    }

    public JsonElement getOptions()
    {
        return this.options;
    }

    public boolean isCoupaingServer()
    {
        return this.isCoupaing;
    }

    public Integer getHubID()
    {
        return hubID;
    }

    public JsonElement getStartupOptions()
    {
        return startupOptions;
    }

    public int getWeight()
    {
        return weight;
    }

    public String getTemplateID() {
        return templateID;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public long getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(long startedTime) {
        this.startedTime = startedTime;
    }

    public UUID getAsker() {
        return asker;
    }

    public void setAsker(UUID asker) {
        this.asker = asker;
    }

    public UUID getClientUUID() {
        return clientUUID;
    }

    public void setClientUUID(UUID clientUUID) {
        this.clientUUID = clientUUID;
    }

    public UUID getMinecraftUUID() {
        return minecraftUUID;
    }

    public void setMinecraftUUID(UUID minecraftUUID) {
        this.minecraftUUID = minecraftUUID;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
