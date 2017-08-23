package net.samagames.hydroangeas.server.commands;

import net.samagames.hydroangeas.common.commands.AbstractCommand;
import net.samagames.hydroangeas.server.HydroangeasServer;

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
public class RefreshCommand extends AbstractCommand
{
    public HydroangeasServer instance;

    public RefreshCommand(HydroangeasServer hydroangeasServer)
    {
        super("refresh");
        this.instance = hydroangeasServer;
    }

    @Override
    public boolean execute(String[] args)
    {
        instance.getLogger().info("Refreshing all clients..");
        instance.getClientManager().globalCheckData();
        return true;
    }

    @Override
    public String getHelp() {
        return "- refresh\n"+
                "Ask to all client to resend their data to the server.";
    }
}