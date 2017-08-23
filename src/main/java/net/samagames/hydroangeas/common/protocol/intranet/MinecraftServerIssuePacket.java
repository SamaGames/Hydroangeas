package net.samagames.hydroangeas.common.protocol.intranet;

import net.samagames.hydroangeas.common.packets.AbstractPacket;

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

        switch (issueType)
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

    public MinecraftServerIssuePacket()
    {

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

    public enum Type
    {
        MAKE, PATCH, START, STOP
    }
}
