package net.samagames.hydroangeas.server.commands;

import net.samagames.hydroangeas.common.commands.AbstractCommand;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.HydroClient;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.server.games.AbstractGameTemplate;

import java.util.List;

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
public class ShutdownCommand extends AbstractCommand
{
    public HydroangeasServer instance;

    public ShutdownCommand(HydroangeasServer hydroangeasServer)
    {
        super("shutdown");
        this.instance = hydroangeasServer;
    }

    @Override
    public boolean execute(String[] args)
    {

        if (args.length == 2)
        {
            switch (args[0])
            {
                case "client":
                    int id;
                    try
                    {
                        id = Integer.parseInt(args[1]);
                        HydroClient client = instance.getClientManager().getClients().get(id);
                        client.shutdown();
                        instance.getLogger().info("#" + id + " shutdown successfully");
                    } catch (NumberFormatException e)
                    {
                        instance.getLogger().info("Erreur numero du client. Tappez: info");
                        return false;
                    } catch (IndexOutOfBoundsException e)
                    {
                        instance.getLogger().info("Erreur mauvais numero du client. Tappez: info");
                        return false;
                    }
                    break;
                case "server":
                    MinecraftServerS server = instance.getClientManager().getServerByName(args[1]);
                    if (server == null)
                    {
                        instance.getLogger().info("Erreur mauvais nom de serveur!");
                        return true;
                    }
                    server.shutdown();
                    instance.getLogger().info(server.getServerName() + " shutdown successfully");
                    break;
                case "template":
                    AbstractGameTemplate template = instance.getTemplateManager().getTemplateByID(args[1]);
                    if(template == null)
                    {
                        instance.getLogger().info("Erreur mauvais nom de template!");
                        return true;
                    }
                    List<MinecraftServerS> serversByTemplate = instance.getClientManager().getServersByTemplate(template);
                    serversByTemplate.forEach(MinecraftServerS::shutdown);

                    instance.getLogger().info(serversByTemplate.size() + " server shutdown successfully");
                    break;
                default:
                    showSyntaxe();
                    break;
            }
        } else
        {
            showSyntaxe();
        }
        return true;
    }

    @Override
    public String getHelp() {
        return "- shutdown <client/server/template> <id/servername/templatename>\n"+
                "Shutdown a client by id provided by the info command or a server by his name.\n"+
                "You can also stop all server with a specific template by using the template name.";
    }

    private void showSyntaxe()
    {
        instance.getLogger().info("Command syntaxe: shutdown {client, server} name");
    }
}
