package net.samagames.hydroangeas.common.packets;

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
public class CommandPacket extends AbstractPacket
{
    private String sourceUUID;
    private String action;

    public CommandPacket()
    {

    }

    public CommandPacket(String source, String action)
    {
        this.sourceUUID = source;
        this.action = action;
    }

    public String getAction()
    {
        return action;
    }


    // TODO: Send logs of the command to the client
    public String getSourceUUID()
    {
        return sourceUUID;
    }
}