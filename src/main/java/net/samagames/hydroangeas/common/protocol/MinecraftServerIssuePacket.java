package net.samagames.hydroangeas.common.protocol;

import net.samagames.hydroangeas.common.informations.MinecraftServerInfos;
import net.samagames.hydroangeas.common.packets.AbstractPacket;

import java.util.UUID;

public class MinecraftServerIssuePacket extends AbstractPacket
{
    private final MinecraftServerInfos serverInfos;
    private final Type issueType;
    private final String message;
    public UUID uuid;
    public MinecraftServerIssuePacket(UUID uuid, MinecraftServerInfos serverInfos, Type issueType)
    {
        this.uuid = uuid;
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

    public UUID getUUID()
    {
        return uuid;
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

    public enum Type { MAKE, PATCH, START, STOP }
}
