package net.samagames.hydroangeas.server.commands;

import net.samagames.hydroangeas.common.commands.AbstractCommand;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.HydroClient;
import net.samagames.hydroangeas.server.client.MinecraftServerS;

import java.util.TreeSet;
import java.util.logging.Level;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 10/07/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class InfosCommand extends AbstractCommand
{

    public HydroangeasServer instance;

    public InfosCommand(HydroangeasServer hydroangeasServer)
    {
        super("info");
        this.instance = hydroangeasServer;
    }

    @Override
    public boolean execute(String[] args)
    {
        if (args.length == 0)
        {
            int i = 0;
            for (HydroClient client : instance.getClientManager().getClients())
            {
                instance.log(Level.INFO, "#" + i + " " + client.getUUID() + ": ");
                instance.log(Level.INFO, "   ip:         " + client.getIp());
                instance.log(Level.INFO, "   weight:     " + client.getActualWeight());
                instance.log(Level.INFO, "   maxWeight:  " + client.getMaxWeight());
                instance.log(Level.INFO, "   Nb server:  " + client.getServerManager().getServers().size());
                instance.log(Level.INFO, "   Last Ping:  " + client.getTimestamp());
                i++;
            }
        } else if (args.length == 1)
        {
            if (args[0].equals("sorted"))
            {
                TreeSet<HydroClient> sortedClient = new TreeSet<>((o1, o2) -> (o1.getAvailableWeight() < o2.getAvailableWeight()) ? -1 : 1);
                sortedClient.addAll(instance.getClientManager().getClients());

                for (HydroClient client : sortedClient)
                {
                    instance.log(Level.INFO, "# " + client.getUUID() + ": ");
                    instance.log(Level.INFO, "   ip:         " + client.getIp());
                    instance.log(Level.INFO, "   weight:     " + client.getActualWeight());
                    instance.log(Level.INFO, "   maxWeight:  " + client.getMaxWeight());
                    instance.log(Level.INFO, "   Nb server:  " + client.getServerManager().getServers().size());
                    instance.log(Level.INFO, "   Last Ping:  " + client.getTimestamp());
                }
                return true;
            }
            int id;
            try
            {
                id = Integer.parseInt(args[0]);
                HydroClient client = instance.getClientManager().getClients().get(id);

                instance.log(Level.INFO, "#" + id + " " + client.getUUID() + ": ");
                instance.log(Level.INFO, "   IP:         " + client.getIp());
                instance.log(Level.INFO, "   Weight:     " + client.getActualWeight());
                instance.log(Level.INFO, "   MaxWeight:  " + client.getMaxWeight());
                instance.log(Level.INFO, "   Nb server:  " + client.getServerManager().getServers().size());
                instance.log(Level.INFO, "   Last Ping:  " + client.getTimestamp());
                instance.log(Level.INFO, "   Servers:    ");

                int i = 0;
                for (MinecraftServerS server : client.getServerManager().getServers())
                {
                    instance.log(Level.INFO, "      #" + i + " Servername: " + server.getServerName());
                    instance.log(Level.INFO, "       Game:     " + server.getGame());
                    instance.log(Level.INFO, "       Map:      " + server.getMap());
                    instance.log(Level.INFO, "       MaxSlots: " + server.getMaxSlot());
                    instance.log(Level.INFO, "       MinSlots: " + server.getMinSlot());
                    instance.log(Level.INFO, "       Weight:   " + server.getWeight());
                    i++;
                }

            } catch (NumberFormatException e)
            {
                instance.log(Level.INFO, "Erreur numero du client.");
                return false;
            } catch (IndexOutOfBoundsException e)
            {
                instance.log(Level.INFO, "Erreur mauvais numero du client.");
                return false;
            }

        }

        return true;
    }
}
