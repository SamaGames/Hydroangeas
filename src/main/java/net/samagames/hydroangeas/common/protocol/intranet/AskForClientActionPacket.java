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
public class AskForClientActionPacket extends AbstractPacket
{

    private UUID uuid;
    private ActionCommand command;

    //Datas
    private String data;

    public AskForClientActionPacket()
    {
    }

    public AskForClientActionPacket(UUID uuid, ActionCommand command, String data)
    {
        this.uuid = uuid;
        this.command = command;
        this.data = data;
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public String getData()
    {
        return data;
    }

    public ActionCommand getCommand()
    {
        return command;
    }

    public enum ActionCommand
    {
        SERVEREND, CLIENTSHUTDOWN, CONSOLECOMMAND
    }
}
