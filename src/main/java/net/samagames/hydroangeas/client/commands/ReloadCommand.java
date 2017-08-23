package net.samagames.hydroangeas.client.commands;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.common.commands.AbstractCommand;

import java.util.logging.Level;

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
public class ReloadCommand extends AbstractCommand
{

    public HydroangeasClient instance;

    public ReloadCommand(HydroangeasClient hydroangeasClient)
    {
        super("reload");
        this.instance = hydroangeasClient;
    }

    @Override
    public boolean execute(String[] args)
    {
        instance.log(Level.INFO, "Reloading..");
        instance.loadConfig();

        return true;
    }

    @Override
    public String getHelp() {
        return null;
    }
}
