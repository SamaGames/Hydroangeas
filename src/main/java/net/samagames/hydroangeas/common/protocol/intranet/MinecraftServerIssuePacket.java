package net.samagames.hydroangeas.common.protocol.intranet;

import net.samagames.hydroangeas.common.packets.AbstractPacket;

import java.util.UUID;

public class MinecraftServerIssuePacket extends AbstractPacket
{
    private Type issueType;
    private String message;
    private UUID uuid;
    private String serverName;

    public MinecraftServerIssuePacket(UUID uuid, String serverName, Type issueType)
    {
        this.uuid = uuid;
        this.serverName = serverName;
        this.issueType = issueType;

        switch(issueType)
        {
            case MAKE:
                this.message = "Impossible de créer le serveur '" + serverName + "'!";
                break;

            case PATCH:
                this.message = "Impossible de patcher le serveur '" + serverName + "'!";
                break;

            case START:
                this.message = "Impossible de démarrer le serveur '" + serverName + "'!";
                break;

            case STOP:
                this.message = "Impossible de stopper le serveur '" + serverName + "'!";
                break;

            default:
                this.message = "Une erreur s'est produite avec le serveur '" + serverName + "'!";
                break;
        }
    }

    public MinecraftServerIssuePacket() {

    }


    public UUID getUUID()
    {
        return uuid;
    }

    public String getServerName()
    {
        return serverName;
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
