package net.samagames.hydroangeas.common.commands;

import net.samagames.hydroangeas.Hydroangeas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 06/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public abstract class CommandManager {

    public Hydroangeas hydroangeas;

    public List<AbstractCommand> commands;

    public CommandManager(Hydroangeas hydroangeas)
    {
        this.hydroangeas = hydroangeas;
        commands = new ArrayList<>();
    }

    public void inputCommand(String data) {

        String[] args = data.split(" ");
        String command = args[0];

        args = Arrays.copyOfRange(args, 1, args.length);

        for(AbstractCommand command1 : commands)
        {
            if(command1.getCommand().equals(command))
            {
                if(!command1.execute(args))
                {
                    hydroangeas.log(Level.WARNING, "Error while executing the command!");
                }
                return;
            }
        }
        hydroangeas.log(Level.INFO, "Command doesn't exist !");
    }
}
