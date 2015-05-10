package net.samagames.hydroangeas.common;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.utils.InternetUtils;

import java.util.UUID;

public class ClientInfos
{
    private final UUID clientUUID;
    private final String dedicatedGame;
    private final String ip;

    public ClientInfos(HydroangeasClient instance)
    {
        this.clientUUID = instance.getClientUUID();
        this.dedicatedGame = instance.getDedicatedGame();
        this.ip = InternetUtils.getExternalIp();
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
}
