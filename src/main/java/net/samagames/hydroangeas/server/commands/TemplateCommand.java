package net.samagames.hydroangeas.server.commands;

import net.samagames.hydroangeas.common.commands.AbstractCommand;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.games.AbstractGameTemplate;

import java.util.List;
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
public class TemplateCommand extends AbstractCommand
{

    public HydroangeasServer instance;

    public TemplateCommand(HydroangeasServer hydroangeasServer)
    {
        super("order");
        this.instance = hydroangeasServer;
    }

    @Override
    public boolean execute(String[] args)
    {
        if (args.length <= 0)
        {
            List<String> listTemplate = instance.getTemplateManager().getListTemplate();
            StringBuilder builder = new StringBuilder("Templates: ");
            listTemplate.forEach((item) -> builder.append(item).append("\n"));

            instance.log(Level.INFO, builder.toString());
        } else
        {
            AbstractGameTemplate template = instance.getTemplateManager().getTemplateByID(args[0]);
            if (template == null)
            {
                instance.log(Level.INFO, "Template not found!");
                return false;
            }
            instance.getAlgorithmicMachine().orderTemplate(template);
        }
        instance.log(Level.INFO, "Done!");
        return true;
    }

    @Override
    public String getHelp() {
        return "- order <Optional: templatename>\n"+
                "With no argument you see all available templates.\n"+
                "Add the desired template name to start a server with this template.";
    }
}
