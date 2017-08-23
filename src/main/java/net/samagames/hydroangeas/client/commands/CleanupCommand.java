package net.samagames.hydroangeas.client.commands;

import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;
import net.samagames.hydroangeas.common.commands.AbstractCommand;
import net.samagames.hydroangeas.common.protocol.intranet.MinecraftServerUpdatePacket;
import org.apache.commons.io.FileDeleteStrategy;

import java.io.File;

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
public class CleanupCommand extends AbstractCommand
{

    public HydroangeasClient instance;

    public CleanupCommand(HydroangeasClient hydroangeasClient)
    {
        super("cleanup");
        this.instance = hydroangeasClient;
    }

    @Override
    public boolean execute(String[] args)
    {
        instance.getLogger().info("Checking servers...");
        File[] files = this.instance.getServerFolder().listFiles();
        for(File file : files)
        {
            String name = file.getName();

            MinecraftServerC serverC = instance.getServerManager().getServerByName(name);
            if(serverC == null)
            {
                instance.getConnectionManager().sendPacket(new MinecraftServerUpdatePacket(instance, name, MinecraftServerUpdatePacket.UType.END));
                instance.getLogger().info("Server: " + name + " not found. Sending shutdown to hydro.");
                try{
                    FileDeleteStrategy.FORCE.delete(file);
                    instance.getLogger().info("Deleted folder!");
                }catch(Exception e)
                {
                    e.printStackTrace();
                    instance.getLogger().info("Can't delete folder!");
                }
            }
        }
        instance.getLogger().info("Check done !");
        return true;
    }

    @Override
    public String getHelp() {
        return null;
    }
}