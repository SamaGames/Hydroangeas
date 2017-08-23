package net.samagames.hydroangeas.server.commands;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.commands.AbstractCommand;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.HydroClient;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.utils.ConsoleColor;

import java.util.TreeSet;
import java.util.logging.Level;

/*
 * This file is part of Hydroangeas.
 *
 * Hydroangeas is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hydroangeas is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hydroangeas.  If not, see <http://www.gnu.org/licenses/>.
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

    @Override
    public String getHelp() {
        return "- info <Optional: client id>\n"+
                "Without arguments it will display the list of connected clients.\n"+
                "Add the client id shown before the uuid to see more info about the client.";
    }

    public static void showHydroClient(HydroClient client, Integer i)
    {
        Hydroangeas.getInstance().log(Level.INFO, "#" + ((i != null)?i:"") + " " + client.getUUID() + ": ");
        Hydroangeas.getInstance().log(Level.INFO, "   ip:         " + ConsoleColor.RED + client.getIp() + ConsoleColor.RESET);
        Hydroangeas.getInstance().log(Level.INFO, "   weight:     " + ConsoleColor.RED + client.getActualWeight() + ConsoleColor.RESET);
        Hydroangeas.getInstance().log(Level.INFO, "   maxWeight:  " + ConsoleColor.RED + client.getMaxWeight() + ConsoleColor.RESET);
        Hydroangeas.getInstance().log(Level.INFO, "   Nb player:  " + ConsoleColor.RED + client.getPlayer() + ConsoleColor.RESET);
        Hydroangeas.getInstance().log(Level.INFO, "   Nb server:  " + ConsoleColor.RED + client.getServerManager().getServers().size() + ConsoleColor.RESET);
        Hydroangeas.getInstance().log(Level.INFO, "   Last Ping:  " + ConsoleColor.RED + client.getTimestamp() + ConsoleColor.RESET);
        Hydroangeas.getInstance().log(Level.INFO, "   Restriction Mode:  " + ConsoleColor.RED + client.getRestrictionMode().getMode() + ConsoleColor.RESET);
        Hydroangeas.getInstance().log(Level.INFO, "   Whitelist:  ");
        for(String data : client.getWhitelist())
        {
            Hydroangeas.getInstance().log(Level.INFO, "   - "+ ConsoleColor.YELLOW + data + ConsoleColor.RESET);
        }
        Hydroangeas.getInstance().log(Level.INFO, "   Blacklist:  ");
        for(String data : client.getBlacklist())
        {
            Hydroangeas.getInstance().log(Level.INFO, "   - "+ ConsoleColor.YELLOW + data + ConsoleColor.RESET);
        }
    }

    public static void showServer(MinecraftServerS server, Integer i)
    {
        Hydroangeas.getInstance().log(Level.INFO, "      #" + ((i != null)?i:"") + " Servername: " + server.getServerName());
        Hydroangeas.getInstance().log(Level.INFO, "       Game:     " + ConsoleColor.RED + server.getGame() + ConsoleColor.RESET);
        Hydroangeas.getInstance().log(Level.INFO, "       Map:      " + ConsoleColor.RED + server.getMap() + ConsoleColor.RESET);
        Hydroangeas.getInstance().log(Level.INFO, "       Players:  " + ConsoleColor.RED + server.getActualSlots() + ConsoleColor.RESET);
        Hydroangeas.getInstance().log(Level.INFO, "       MaxSlots: " + ConsoleColor.RED + server.getMaxSlot() + ConsoleColor.RESET);
        Hydroangeas.getInstance().log(Level.INFO, "       MinSlots: " + ConsoleColor.RED + server.getMinSlot() + ConsoleColor.RESET);
        Hydroangeas.getInstance().log(Level.INFO, "       Weight:   " + ConsoleColor.RED + server.getWeight() + ConsoleColor.RESET);
    }
}
