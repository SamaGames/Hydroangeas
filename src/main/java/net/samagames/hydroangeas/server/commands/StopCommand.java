package net.samagames.hydroangeas.server.commands;

import net.samagames.hydroangeas.common.commands.AbstractCommand;
import net.samagames.hydroangeas.server.HydroangeasServer;

import java.util.logging.Level;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 06/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class StopCommand extends AbstractCommand
{

    public HydroangeasServer instance;

    public StopCommand(HydroangeasServer hydroangeasServer)
    {
        super("stop");
        this.instance = hydroangeasServer;
    }

    @Override
    public boolean execute(String[] args)
    {

        instance.log(Level.INFO, "Stopping the server..");
        System.exit(0);

        return true;
    }
}
