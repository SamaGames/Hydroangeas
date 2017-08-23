package net.samagames.hydroangeas.client.commands;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.common.commands.CommandManager;

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
public class ClientCommandManager extends CommandManager
{

    public HydroangeasClient instance;

    public ClientCommandManager(Hydroangeas hydroangeas)
    {
        super(hydroangeas);
        instance = hydroangeas.getAsClient();

        commands.add(new StopCommand(instance));
        commands.add(new CleanupCommand(instance));
        commands.add(new ReloadCommand(instance));
    }
}
