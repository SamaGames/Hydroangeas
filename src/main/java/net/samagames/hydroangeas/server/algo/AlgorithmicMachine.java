package net.samagames.hydroangeas.server.algo;

import net.samagames.hydroangeas.common.protocol.intranet.MinecraftServerUpdatePacket;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.HydroClient;
import net.samagames.hydroangeas.server.client.MinecraftServerS;
import net.samagames.hydroangeas.server.data.Status;
import net.samagames.hydroangeas.server.games.BasicGameTemplate;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class AlgorithmicMachine
{
    private static final int FREE_SPACE = 5;
    private final HydroangeasServer instance;

    public AlgorithmicMachine(HydroangeasServer instance)
    {
        this.instance = instance;
    }

    public void startMachinery()
    {
        //USELESS
        ModMessage.sendMessage(InstanceType.SERVER, "> Prêt !");
    }

    public HydroClient selectGoodHydroClient(BasicGameTemplate template)
    {
        TreeSet<HydroClient> sortedClient = new TreeSet<>((o1, o2) -> (o1.getAvailableWeight() < o2.getAvailableWeight())? -1 : 1);
        sortedClient.addAll(instance.getClientManager().getClients());
        for(HydroClient client : sortedClient)
        {
            int weight = template.getWeight();
            if(client.getAvailableWeight() - weight > FREE_SPACE)
            {
                return client;
            }
        }
        return null;
    }

    public MinecraftServerS orderBasic(String game, String map)
    {
        BasicGameTemplate template = instance.getTemplateManager().getTemplateByGameAndMap(game, map);
        if(template == null)
        {
            instance.getLogger().warning("Error template " + game + " " + map + " doesn't exist!");
            return null;
        }
        return orderTemplate(template);
    }

    public MinecraftServerS orderTemplate(BasicGameTemplate template)
    {
        HydroClient client = selectGoodHydroClient(template);

        if(client == null)
        {
            instance.log(Level.SEVERE, "Major error ! No Hydroclient available !");
            return null;
        }

        MinecraftServerS server = client.getServerManager().addServer(
                template.getGameName(),
                template.getMapName(),
                template.getMinSlot(),
                template.getMaxSlot(),
                template.getOptions(),
                template.isCoupaing(),
                template.getId());
        instance.log(Level.INFO, template.toString() + " created on " + client.getIp());
        return server;
    }

    public MinecraftServerS getServerFor(BasicGameTemplate template)
    {
        return null;
    }

    public void onServerUpdate(MinecraftServerUpdatePacket serverStatus)
    {
        if(serverStatus.getAction().equals(MinecraftServerUpdatePacket.UType.END))
        {
            HydroClient client = instance.getClientManager().getClientByUUID(serverStatus.getUUID());

            instance.log(Level.INFO, "Server ended on " + client.getIp() + " servername: " + serverStatus.getServerName());
        }
    }

    public List<MinecraftServerS> getServerByTemplatesAndAvailable(String templateID)
    {
        List<MinecraftServerS> servers = new ArrayList<>();
        for(HydroClient client : instance.getClientManager().getClients())
        {
            servers.addAll(client.getServerManager().getServers().stream().filter(server -> server.getTemplateID().equalsIgnoreCase(templateID) && (server.getStatus().isAllowJoin() || server.getStatus().equals(Status.STARTING)) && server.getActualSlots() < server.getMaxSlot() * 0.90).collect(Collectors.toList()));
        }
        return servers;
    }
}
