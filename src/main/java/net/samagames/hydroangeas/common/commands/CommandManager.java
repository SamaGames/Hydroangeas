package net.samagames.hydroangeas.common.commands;

import net.samagames.hydroangeas.Hydroangeas;

import java.util.ArrayList;
import java.util.Arrays;
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
public abstract class CommandManager
{

    public Hydroangeas hydroangeas;

    public List<AbstractCommand> commands;

    public CommandManager(Hydroangeas hydroangeas)
    {
        this.hydroangeas = hydroangeas;
        commands = new ArrayList<>();
    }

    public void inputCommand(String data)
    {

        String[] args = data.split(" ");
        String command = args[0];

        args = Arrays.copyOfRange(args, 1, args.length);

        if (command.equals("help"))
        {
            showHelp();
            return;
        }

        for (AbstractCommand command1 : commands)
        {
            if (command1.getCommand().equals(command))
            {
                if (!command1.execute(args))
                {
                    hydroangeas.log(Level.WARNING, "Error while executing the command!");
                }
                return;
            }
        }
        hydroangeas.log(Level.INFO, "Command doesn't exist !");
    }

    public void showHelp()
    {
        //Please no lambda !
        for(AbstractCommand command : commands)
        {
            String help = command.getHelp();
            if(help != null)
            {
                hydroangeas.getLogger().info(help);
            }
        }
    }
}
