package net.samagames.hydroangeas.common.informations;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.servers.MinecraftServer;
import net.samagames.hydroangeas.utils.InternetUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClientInfos
{
    private final UUID clientUUID;
    private final String ip;
    private final int maxWeight;
    private final int actualWeight;
    private final Timestamp timestamp;
    private final ArrayList<MinecraftServerInfos> serverInfos;

    public ClientInfos(HydroangeasClient instance)
    {
        this.clientUUID = instance.getClientUUID();
        this.ip = InternetUtils.getExternalIp();
        this.maxWeight = instance.getMaxWeight();
        this.timestamp = new Timestamp(new Date().getTime());

        // TODO: Actual Weight calc
        this.actualWeight = 0;

        this.serverInfos = new ArrayList<>();
        this.serverInfos.addAll(instance.getServerManager().getServers().values().stream().map(MinecraftServer::getServerInfos).collect(Collectors.toList()));
    }

    public UUID getClientUUID()
    {
        return this.clientUUID;
    }

    public String getIp()
    {
        return this.ip;
    }

    public int getMaxWeight()
    {
        return this.maxWeight;
    }

    public int getActualWeight()
    {
        return this.actualWeight;
    }

    public Timestamp getTimestamp()
    {
        return this.timestamp;
    }

    public ArrayList<MinecraftServerInfos> getServersInfos()
    {
        return this.serverInfos;
    }
}
