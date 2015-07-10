package net.samagames.hydroangeas.server.commands;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.commands.CommandManager;
import net.samagames.hydroangeas.server.HydroangeasServer;


/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 06/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class ServerCommandManager extends CommandManager {

    public HydroangeasServer instance;

    public ServerCommandManager(Hydroangeas hydroangeas) {
        super(hydroangeas);
        instance = hydroangeas.getAsServer();

        commands.add(new StopCommand(instance));
        commands.add(new TemplateCommand(instance));
        commands.add(new InfosCommand(instance));
    }
}
