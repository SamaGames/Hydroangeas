package net.samagames.hydroangeas.server.commands;

import net.samagames.hydroangeas.common.commands.AbstractCommand;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.HydroClient;
import net.samagames.hydroangeas.server.client.MinecraftServerS;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 11/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class ShutdownCommand extends AbstractCommand {
    public HydroangeasServer instance;

    public ShutdownCommand(HydroangeasServer hydroangeasServer) {
        super("shutdown");
        this.instance = hydroangeasServer;
    }

    @Override
    public boolean execute(String[] args) {

        if(args.length == 2)
        {
            if(args[0].equals("client"))
            {
                int id;
                try{
                    id = Integer.valueOf(args[1]);
                    HydroClient client = instance.getClientManager().getClients().get(id);
                    client.shutdown();
                    instance.getLogger().info("#" + id + " shutdown successfully");
                }catch (NumberFormatException e)
                {
                    instance.getLogger().info("Erreur numero du client. Tappez: info");
                    return false;
                }catch (IndexOutOfBoundsException e)
                {
                    instance.getLogger().info("Erreur mauvais numero du client. Tappez: info");
                    return false;
                }
            }else if(args[0].equals("server"))
            {
                MinecraftServerS server = instance.getClientManager().getServerByName(args[1]);
                if(server == null)
                {
                    instance.getLogger().info("Erreur mauvais nom de serveur!");
                    return true;
                }
                server.shutdown();
                instance.getLogger().info(server.getServerName() + " shutdown successfully");
            }else{
                showSyntaxe();
            }
        }else{
            showSyntaxe();
        }
        return true;
    }

    private void showSyntaxe()
    {
        instance.getLogger().info("Command syntaxe: shutdown {client, server} name");
    }
}
