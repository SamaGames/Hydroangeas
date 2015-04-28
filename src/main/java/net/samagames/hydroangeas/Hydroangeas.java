package net.samagames.hydroangeas;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class Hydroangeas
{
    private static Hydroangeas instance;

    public Hydroangeas(OptionSet options)
    {

    }

    public static void main(String[] args)
    {
        OptionParser parser = new OptionParser()
        {
            {
                acceptsAll(Arrays.asList("?", "help"), "Show the help");

                acceptsAll(Arrays.asList("c", "config"), "Configuration file in json")
                        .withRequiredArg()
                        .ofType(String.class);

                acceptsAll(Arrays.asList("d", "default"), "Create a default configuration file");

                acceptsAll(Collections.singletonList("debug"), "First jar with original names, for generating mapping");

                acceptsAll(Arrays.asList("v", "version"), "Displays version information");
            }
        };

        try
        {
            OptionSet options = parser.parse(args);

            if (options == null || options.has("?"))
            {
                try
                {
                    parser.printHelpOn(System.out);
                }
                catch (IOException ex)
                {
                    System.out.println(ex.getLocalizedMessage());
                }

                System.exit(0);
                return;
            }

            if (options.has("version"))
            {
                System.out.println("Hydroangeas version 1.0.0 by BlueSlime");

                System.exit(0);
                return;
            }

            new Hydroangeas(options);
        }
        catch (OptionException ex)
        {
            System.out.println(ex.getLocalizedMessage());
            System.exit(-1);
        }
    }
}
