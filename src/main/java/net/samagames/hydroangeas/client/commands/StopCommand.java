package net.samagames.hydroangeas.client.commands;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.common.commands.AbstractCommand;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 06/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class StopCommand extends AbstractCommand
{

    public HydroangeasClient instance;

    public StopCommand(HydroangeasClient hydroangeasClient)
    {
        super("stop");
        this.instance = hydroangeasClient;
    }

    @Override
    public boolean execute(String[] args)
    {

        instance.getLogger().info("Stopping the server..");
        System.exit(0);

        return true;
    }
}
