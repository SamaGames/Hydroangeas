package net.samagames.hydroangeas.common.informations;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.servers.MinecraftServer;
import net.samagames.hydroangeas.utils.InternetUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class ClientInfos
{
    private final String clientName;
    private final String dedicatedGame;
    private final String ip;
    private final int maxInstances;
    private final Timestamp timestamp;
    private final ArrayList<MinecraftServerInfos> serverInfos;

    public ClientInfos(HydroangeasClient instance)
    {
        this.clientName = instance.getClientName();
        this.dedicatedGame = instance.getDedicatedGame();
        this.ip = InternetUtils.getExternalIp();
        this.maxInstances = instance.getMaxInstances();
        this.timestamp = new Timestamp(new Date().getTime());

        this.serverInfos = new ArrayList<>();
        this.serverInfos.addAll(instance.getServerManager().getServers().values().stream().map(MinecraftServer::getServerInfos).collect(Collectors.toList()));
    }

    public String getClientName()
    {
        return this.clientName;
    }

    public String getDedicatedGame()
    {
        return this.dedicatedGame;
    }

    public String getIp()
    {
        return this.ip;
    }

    public int getMaxInstances()
    {
        return this.maxInstances;
    }

    public Timestamp getTimestamp()
    {
        return this.timestamp;
    }

    public ArrayList<MinecraftServerInfos> getServerInfos()
    {
        return this.serverInfos;
    }
}
