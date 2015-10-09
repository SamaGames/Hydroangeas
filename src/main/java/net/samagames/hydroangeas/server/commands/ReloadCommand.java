package net.samagames.hydroangeas.server.commands;

import net.samagames.hydroangeas.common.commands.AbstractCommand;
import net.samagames.hydroangeas.server.HydroangeasServer;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class ReloadCommand extends AbstractCommand
{

    private final HydroangeasServer server;
    public ReloadCommand(HydroangeasServer server)
    {
        super("reload");
        this.server = server;
    }

    @Override
    public boolean execute(String[] args)
    {
        server.getTemplateManager().reload();
        return true;
    }
}
