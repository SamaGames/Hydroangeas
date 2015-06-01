package net.samagames.hydroangeas.client.packets;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.common.informations.ClientInfos;
import net.samagames.hydroangeas.common.informations.MinecraftServerInfos;
import net.samagames.hydroangeas.common.packets.AbstractPacket;

public class MinecraftServerIssuePacket extends AbstractPacket
{
    public enum Type { MAKE, PATCH, START, STOP };

    private final ClientInfos clientInfos;
    private final MinecraftServerInfos serverInfos;
    private final Type issueType;
    private final String message;

    public MinecraftServerIssuePacket(HydroangeasClient instance, MinecraftServerInfos serverInfos, Type issueType)
    {
        this.clientInfos = new ClientInfos(instance);
        this.serverInfos = serverInfos;
        this.issueType = issueType;

        switch(issueType)
        {
            case MAKE:
                this.message = "Impossible de créer le serveur '" + serverInfos.getServerName() + "'!";
                break;

            case PATCH:
                this.message = "Impossible de patcher le serveur '" + serverInfos.getServerName() + "'!";
                break;

            case START:
                this.message = "Impossible de démarrer le serveur '" + serverInfos.getServerName() + "'!";
                break;

            case STOP:
                this.message = "Impossible de stopper le serveur '" + serverInfos.getServerName() + "'!";
                break;

            default:
                this.message = "Une erreur s'est produite avec le serveur '" + serverInfos.getServerName() + "'!";
                break;
        }
    }

    public ClientInfos getClientInfos()
    {
        return this.clientInfos;
    }

    public MinecraftServerInfos getServerInfos()
    {
        return this.serverInfos;
    }

    public Type getIssueType()
    {
        return this.issueType;
    }

    public String getMessage()
    {
        return this.message;
    }

    @Override
    public String getChannel()
    {
        return "issue@hydroangeas-server";
    }
}
