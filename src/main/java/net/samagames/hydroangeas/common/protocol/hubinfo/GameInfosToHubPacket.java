package net.samagames.hydroangeas.common.protocol.hubinfo;

import net.samagames.hydroangeas.common.packets.AbstractPacket;

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
public class GameInfosToHubPacket extends AbstractPacket
{

    private int playerMaxForMap;
    private int playerWaitFor;
    private int totalPlayerOnServers;

    private String templateID;

    public GameInfosToHubPacket()
    {
    }

    public GameInfosToHubPacket(String templateID)
    {
        this.templateID = templateID;
    }

    public int getPlayerMaxForMap()
    {
        return playerMaxForMap;
    }

    public void setPlayerMaxForMap(int playerMaxForMap)
    {
        this.playerMaxForMap = playerMaxForMap;
    }

    public int getPlayerWaitFor()
    {
        return playerWaitFor;
    }

    public void setPlayerWaitFor(int playerWaitFor)
    {
        this.playerWaitFor = playerWaitFor;
    }

    public int getTotalPlayerOnServers()
    {
        return totalPlayerOnServers;
    }

    public void setTotalPlayerOnServers(int totalPlayerOnServers)
    {
        this.totalPlayerOnServers = totalPlayerOnServers;
    }

    public String getTemplateID()
    {
        return templateID;
    }
}
