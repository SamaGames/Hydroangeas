package net.samagames.hydroangeas.server.commands;

import net.samagames.hydroangeas.common.commands.AbstractCommand;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.games.BasicGameTemplate;

import java.util.List;
import java.util.logging.Level;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 10/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class TemplateCommand extends AbstractCommand {

    public HydroangeasServer instance;

    public TemplateCommand(HydroangeasServer hydroangeasServer) {
        super("order");
        this.instance = hydroangeasServer;
    }

    @Override
    public boolean execute(String[] args) {

        if (args.length <= 0)
        {
            List<String> listTemplate = instance.getAlgorithmicMachine().getListTemplate();
            String message = "Templates: ";
            for(String templateName : listTemplate)
            {
                message += templateName;
            }

            instance.log(Level.INFO, message);
        }else if(args.length >= 1)
        {
            BasicGameTemplate template = instance.getAlgorithmicMachine().getTemplateByname(args[0]);
            if(template == null)
            {
                instance.log(Level.INFO, "Template not found!");
                return false;
            }
            instance.getAlgorithmicMachine().orderTemplate(template);
        }
        instance.log(Level.INFO, "Done!");
        return true;
    }
}
