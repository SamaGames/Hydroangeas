package net.samagames.hydroangeas;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.server.HydroangeasServer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

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
                acceptsAll(Collections.singletonList("debug"), "Debug flag");
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
                }
                catch (IOException ex)
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

            if (!options.has("client") && !options.has("server"))
            {
                System.err.println("You must start Hydroangeas as a client or a server!");
                System.exit(-1);
            }
            else if (options.has("client") && options.has("server"))
            {
                System.err.println("Hydroangeas can't be a client AND a server!");
                System.exit(-1);
            }

            if (options.has("client"))
                new HydroangeasClient(options);
            else if (options.has("server"))
                new HydroangeasServer(options);
        }
        catch (OptionException ex)
        {
            System.err.println(ex.getLocalizedMessage());
            System.exit(-1);
        }
    }
}
