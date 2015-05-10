package net.samagames.hydroangeas.common;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.utils.InternetUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class ClientInfos
{
    private final UUID clientUUID;
    private final String dedicatedGame;
    private final String ip;
    private final Timestamp timestamp;

    public ClientInfos(HydroangeasClient instance)
    {
        this.clientUUID = instance.getClientUUID();
        this.dedicatedGame = instance.getDedicatedGame();
        this.ip = InternetUtils.getExternalIp();
        this.timestamp = new Timestamp(new Date().getTime());
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
}
