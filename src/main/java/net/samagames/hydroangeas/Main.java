package net.samagames.hydroangeas;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.server.HydroangeasServer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

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
public class Main
{
    public static void main(String[] args)
    {
        OptionParser parser = new OptionParser()
        {
            {
                acceptsAll(Arrays.asList("?", "help"), "Show the help");
                acceptsAll(Collections.singletonList("client"), "Be the client");
                acceptsAll(Collections.singletonList("server"), "Be the server");

                acceptsAll(Arrays.asList("c", "config"), "Configuration file")
                        .withRequiredArg()
                        .ofType(String.class);

                acceptsAll(Arrays.asList("d", "default"), "Create a default configuration file");
                acceptsAll(Arrays.asList("v", "version"), "Displays version information");
            }
        };

        try
        {
            OptionSet options = parser.parse(args);

            if (options == null || !options.hasOptions() || options.has("?"))
            {
                try
                {
                    parser.printHelpOn(System.out);
                } catch (IOException ex)
                {
                    System.err.println(ex.getLocalizedMessage());
                }

                System.exit(0);
                return;
            }

            if (options.has("version"))
            {
                System.exit(0);
                return;
            }

            if (!options.has("c") && !options.has("d"))
            {
                System.err.println("You most provide a configuration file!");
                System.exit(-1);
            }

            if (!options.has("client") && !options.has("server"))
            {
                System.err.println("You must start Hydroangeas as a client or a server!");
                System.exit(6);
            } else if (options.has("client") && options.has("server"))
            {
                System.err.println("Hydroangeas can't be a client AND a server!");
                System.exit(7);
            }

            Hydroangeas hydroangeas;

            if (options.has("server"))
                hydroangeas = new HydroangeasServer(options);
            else //if (options.has("client"))
                hydroangeas = new HydroangeasClient(options);

            while (hydroangeas.isRunning)
            {
                String line = null;
                try
                {
                    line = hydroangeas.getConsoleReader().readLine(">");
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                if (line != null)
                {
                    hydroangeas.getCommandManager().inputCommand(line);
                }
            }
        } catch (OptionException | IOException ex)
        {
            System.err.println(ex.getLocalizedMessage());
            System.exit(42);
        }
    }
}
