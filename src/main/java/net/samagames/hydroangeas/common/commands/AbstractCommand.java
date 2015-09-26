package net.samagames.hydroangeas.common.commands;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 06/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public abstract class AbstractCommand
{

    protected String command;

    public AbstractCommand(String command)
    {
        this.command = command;
    }

    public abstract boolean execute(String[] args);

    public String getCommand()
    {
        return command;
    }
}
