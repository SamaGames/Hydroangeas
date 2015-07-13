package net.samagames.hydroangeas.server.algo;

import net.samagames.hydroangeas.common.protocol.MinecraftServerUpdatePacket;
import net.samagames.hydroangeas.server.HydroangeasServer;
import net.samagames.hydroangeas.server.client.HydroClient;
import net.samagames.hydroangeas.server.games.BasicGameTemplate;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.ModMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;

public class AlgorithmicMachine
{
    private static final int FREE_SPACE = 5;
    private final HydroangeasServer instance;
    private HashMap<String, BasicGameTemplate> templates = new HashMap<>();

    public AlgorithmicMachine(HydroangeasServer instance)
    {
        this.instance = instance;

        templates.put("quake", new BasicGameTemplate("quake", "quake", "babylon", 2, 10, new HashMap<>()));
    }

    public void startMachinery()
    {
        //USELESS
        ModMessage.sendMessage(InstanceType.SERVER, "> Prêt !");
    }

    public HydroClient selectGoodHydroClient(BasicGameTemplate template)
    {
        TreeSet<HydroClient> sortedClient = new TreeSet<>((o1, o2) -> (o1.getAvailableWeight() < o2.getAvailableWeight())? -1:1);
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

    public boolean orderTemplate(BasicGameTemplate template)
    {
        HydroClient client = selectGoodHydroClient(template);

        if(client == null)
        {
            instance.log(Level.SEVERE, "Major error ! No Hydroclient available !");
            return false;
        }

        client.getServerManager().addServer(
                template.getGameName(),
                template.getMapName(),
                template.getMinSlot(),
                template.getMaxSlot(),
                template.getOptions(),
                template.isCoupaing(),
                template.getId());
        instance.log(Level.INFO, template.toString() + " created on " + client.getIp());
        return true;
    }

    public void onServerUpdate(MinecraftServerUpdatePacket serverStatus)
    {
        if(serverStatus.getAction().equals(MinecraftServerUpdatePacket.UType.END))
        {
            HydroClient client = instance.getClientManager().getClientByUUID(serverStatus.getUUID());
            /*MinecraftServerS oldserver = client.getServerManager().getServerByName(serverStatus.getServerName());
            client.getServerManager().addServer(oldserver.getGame(),
                    oldserver.getMap(),
                    oldserver.getMinSlot(), //We restart the same server on the same client for test
                    oldserver.getMaxSlot(),
                    oldserver.getOptions(),
                    oldserver.isCoupaingServer());*/

            instance.log(Level.INFO, "Server ended on " + client.getIp() + " servername: " + serverStatus.getServerName());
        }
    }

    public BasicGameTemplate getTemplateByname(String name)
    {
        return templates.get(name);
    }

    public HashMap<String, BasicGameTemplate> getTemplates() {
        return templates;
    }

    public List<String> getListTemplate()
    {
        ArrayList<String> tmp = new ArrayList<>();
        tmp.addAll(templates.keySet());
        return tmp;
    }
}
