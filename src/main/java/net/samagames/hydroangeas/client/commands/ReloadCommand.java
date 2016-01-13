package net.samagames.hydroangeas.client.commands;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.common.commands.AbstractCommand;

import java.util.logging.Level;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 13/01/2016.
 * (C) Copyright Elydra Network 2015-16
 * All rights reserved.
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
