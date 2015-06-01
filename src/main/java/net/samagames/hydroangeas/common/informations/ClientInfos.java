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
    private final String dedicatedGame;
    private final String ip;
    private final Timestamp timestamp;
    private final ArrayList<MinecraftServerInfos> serverInfos;

    public ClientInfos(HydroangeasClient instance)
    {
        this.clientUUID = instance.getClientUUID();
        this.dedicatedGame = instance.getDedicatedGame();
        this.ip = InternetUtils.getExternalIp();
        this.timestamp = new Timestamp(new Date().getTime());

        this.serverInfos = new ArrayList<>();
        this.serverInfos.addAll(instance.getServerManager().getServers().values().stream().map(MinecraftServer::getServerInfos).collect(Collectors.toList()));
    }

    public UUID getClientUUID()
    {
        return this.clientUUID;
    }

    public String getDedicatedGame()
    {
        return this.dedicatedGame;
    }

    public String getIp()
    {
        return this.ip;
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
