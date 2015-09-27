package net.samagames.hydroangeas.client.commands;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.common.commands.CommandManager;


/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 06/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class ClientCommandManager extends CommandManager
{

    public HydroangeasClient instance;

    public ClientCommandManager(Hydroangeas hydroangeas)
    {
        super(hydroangeas);
        instance = hydroangeas.getAsClient();

        commands.add(new StopCommand(instance));
    }
}
