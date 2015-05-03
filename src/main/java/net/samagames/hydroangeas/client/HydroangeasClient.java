package net.samagames.hydroangeas.client;

import joptsimple.OptionSet;
import net.samagames.hydroangeas.Hydroangeas;

import java.util.logging.Level;

public class HydroangeasClient extends Hydroangeas
{
    public HydroangeasClient(OptionSet options)
    {
        super(options);
    }

    @Override
    public void enable()
    {
        this.log(Level.INFO, "Starting Hydroangeas client...");
    }
}
