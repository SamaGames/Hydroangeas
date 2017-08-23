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
public class ReloadCommand extends AbstractCommand
{

    private final HydroangeasServer server;
    public ReloadCommand(HydroangeasServer server)
    {
        super("reload");
        this.server = server;
    }

    @Override
    public boolean execute(String[] args)
    {
        server.getTemplateManager().reload();
        return true;
    }

    @Override
    public String getHelp() {
        return "- reload\n"
                + "Reload server (Actually only templates)";
    }
}
