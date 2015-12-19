package net.samagames.hydroangeas.server.algo;

import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.protocol.intranet.MinecraftServerUpdatePacket;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.HydroClient;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.server.data.Status;
import net.samagames.hydroangeas.server.games.AbstractGameTemplate;
import net.samagames.hydroangeas.utils.ConsoleColor;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class AlgorithmicMachine
{
    private static final int FREE_SPACE = 0;
    private final HydroangeasServer instance;

    public AlgorithmicMachine(HydroangeasServer instance)
    {
        this.instance = instance;
    }

    public HydroClient selectGoodHydroClient(AbstractGameTemplate template)
    {
        TreeSet<HydroClient> sortedClient = new TreeSet<>((o1, o2) -> (o1.getAvailableWeight() > o2.getAvailableWeight()) ? -1 : 1);
        sortedClient.addAll(instance.getClientManager().getClients());
        for (HydroClient client : sortedClient)
        {
            int weight = template.getWeight();
            if (client.getAvailableWeight() - weight >= FREE_SPACE && hasNoRestriction(client, template))
            {
                return client;
            }
        }
        return null;
    }

    public MinecraftServerS orderBasic(String game, String map)
    {
        AbstractGameTemplate template = instance.getTemplateManager().getTemplateByGameAndMap(game, map);
        if (template == null)
        {
            instance.getLogger().warning("Error template " + game + " " + map + " doesn't exist!");
            return null;
        }
        return orderTemplate(template);
    }

    public MinecraftServerS orderTemplate(AbstractGameTemplate template)
    {
        HydroClient client = selectGoodHydroClient(template);

        if (client == null)
        {
            instance.log(Level.SEVERE, ConsoleColor. RED + "No Hydroclient available !"+  ConsoleColor.RESET);
            return null;
        }

        MinecraftServerS server = client.getServerManager().addServer(template, template.getGameName().toLowerCase().startsWith("hub"));
        instance.log(Level.INFO, template + " created on " + client.getIp());
        return server;
    }

    public void onServerUpdate(HydroClient client, MinecraftServerS oldServer, MinecraftServerUpdatePacket serverStatus)
    {
        if (serverStatus.getAction().equals(MinecraftServerUpdatePacket.UType.END))
        {
            instance.log(Level.INFO, "Server ended on " + client.getIp() + " servername: " + serverStatus.getServerName());

        }
    }

    public List<MinecraftServerS> getServerByTemplatesAndAvailable(String templateID)
    {
        List<MinecraftServerS> servers = new ArrayList<>();
        for (HydroClient client : instance.getClientManager().getClients())
        {
            servers.addAll(client.getServerManager().getServers().stream().filter(server -> server.getTemplateID().equalsIgnoreCase(templateID) && (server.getStatus().isAllowJoin() || server.getStatus().equals(Status.STARTING)) && server.getActualSlots() < server.getMaxSlot() * 0.90).collect(Collectors.toList()));
        }
        return servers;
    }

    private boolean hasNoRestriction(HydroClient client, AbstractGameTemplate template)
    {
        //useless but in mainly cases we do only one check
        if(client.getRestrictionMode().equals(Hydroangeas.RestrictionMode.NONE))
            return true;

        if(client.getRestrictionMode().equals(Hydroangeas.RestrictionMode.WHITELIST))
        {
            if(client.getWhitelist().contains(template.getId()))
            {
                return true;
            }
            return false;
        }

        if(client.getRestrictionMode().equals(Hydroangeas.RestrictionMode.BLACKLIST))
        {
            if(client.getBlacklist().contains(template.getId()))
            {
                return false;
            }
            return true;
        }

        return true;
    }
}
