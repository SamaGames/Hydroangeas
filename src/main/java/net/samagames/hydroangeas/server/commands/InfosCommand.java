package net.samagames.hydroangeas.server.commands;

import net.samagames.hydroangeas.common.commands.AbstractCommand;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.HydroClient;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.utils.ConsoleColor;

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
                showHydroClient(client, i);
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
                    showHydroClient(client, null);
                }
                return true;
            }
            int id;
            try
            {
                id = Integer.parseInt(args[0]);
                HydroClient client = instance.getClientManager().getClients().get(id);

                showHydroClient(client, id);
                instance.log(Level.INFO, "   Servers:    ");

                int i = 0;
                for (MinecraftServerS server : client.getServerManager().getServers())
                {
                    showServer(server, i);
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

    public void showHydroClient(HydroClient client, Integer i)
    {
        instance.log(Level.INFO, "#" + ((i != null)?i:"") + " " + client.getUUID() + ": ");
        instance.log(Level.INFO, "   ip:         " + ConsoleColor.RED + client.getIp() + ConsoleColor.RESET);
        instance.log(Level.INFO, "   weight:     " + ConsoleColor.RED + client.getActualWeight() + ConsoleColor.RESET);
        instance.log(Level.INFO, "   maxWeight:  " + ConsoleColor.RED + client.getMaxWeight() + ConsoleColor.RESET);
        instance.log(Level.INFO, "   Nb player:  " + ConsoleColor.RED + client.getPlayer() + ConsoleColor.RESET);
        instance.log(Level.INFO, "   Nb server:  " + ConsoleColor.RED + client.getServerManager().getServers().size() + ConsoleColor.RESET);
        instance.log(Level.INFO, "   Last Ping:  " + ConsoleColor.RED + client.getTimestamp() + ConsoleColor.RESET);
        instance.log(Level.INFO, "   Restriction Mode:  " + ConsoleColor.RED + client.getRestrictionMode().getMode() + ConsoleColor.RESET);
        instance.log(Level.INFO, "   Whitelist:  ");
        for(String data : client.getWhitelist())
        {
            instance.log(Level.INFO, "   - "+ ConsoleColor.YELLOW + data + ConsoleColor.RESET);
        }
        instance.log(Level.INFO, "   Blacklist:  ");
        for(String data : client.getBlacklist())
        {
            instance.log(Level.INFO, "   - "+ ConsoleColor.YELLOW + data + ConsoleColor.RESET);
        }
    }

    public void showServer(MinecraftServerS server, Integer i)
    {
        instance.log(Level.INFO, "      #" + ((i != null)?i:"") + " Servername: " + server.getServerName());
        instance.log(Level.INFO, "       Game:     " + ConsoleColor.RED + server.getGame() + ConsoleColor.RESET);
        instance.log(Level.INFO, "       Map:      " + ConsoleColor.RED + server.getMap() + ConsoleColor.RESET);
        instance.log(Level.INFO, "       Players:  " + ConsoleColor.RED + server.getActualSlots() + ConsoleColor.RESET);
        instance.log(Level.INFO, "       MaxSlots: " + ConsoleColor.RED + server.getMaxSlot() + ConsoleColor.RESET);
        instance.log(Level.INFO, "       MinSlots: " + ConsoleColor.RED + server.getMinSlot() + ConsoleColor.RESET);
        instance.log(Level.INFO, "       Weight:   " + ConsoleColor.RED + server.getWeight() + ConsoleColor.RESET);
    }
}
