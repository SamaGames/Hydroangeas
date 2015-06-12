package net.samagames.hydroangeas.server;

import net.samagames.hydroangeas.client.packets.HelloClientPacket;
import net.samagames.hydroangeas.common.informations.ClientInfos;
import net.samagames.hydroangeas.common.informations.MinecraftServerInfos;
import net.samagames.hydroangeas.server.packets.HelloServerPacket;
import net.samagames.hydroangeas.server.scheduler.KeepUpdatedThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class ClientManager
{
    private final HydroangeasServer instance;
    private final HashMap<String, ClientInfos> clients;
    private final KeepUpdatedThread keepUpdatedThread;

    public ClientManager(HydroangeasServer instance)
    {
        this.instance = instance;
        this.clients = new HashMap<>();

        this.keepUpdatedThread = new KeepUpdatedThread(instance);
        this.keepUpdatedThread.start();
    }

    public void onClientHeartbeat(HelloClientPacket packet)
    {
        if(!this.clients.containsKey(packet.getClientInfos().getClientName()))
        {
            String dedicatedGame = (packet.getClientInfos().getDedicatedGame() == null ? "No dedicated game" : "Dedicated game is " + packet.getClientInfos().getDedicatedGame());
            this.instance.log(Level.INFO, "Client " + packet.getClientInfos().getClientName() + " connected! " + dedicatedGame + ".");
        }

        this.clients.put(packet.getClientInfos().getClientName(), packet.getClientInfos());

        new HelloServerPacket(packet).send();
    }

    public void onClientNoReachable(String clientName)
    {
        if(this.clients.containsKey(clientName))
            this.clients.remove(clientName);
    }

    public ClientInfos getClientInfosByUUID(String clientName)
    {
        if(this.clients.containsKey(clientName))
            return this.clients.get(clientName);
        else
            return null;
    }

    public ArrayList<ClientInfos> getClientsByGame(String game)
    {
        ArrayList<ClientInfos> temp = new ArrayList<>();

        for(ClientInfos infos : this.clients.values())
            if(infos.getDedicatedGame().equals(game))
                temp.add(infos);

        return temp;
    }

    public int getStartedServersForThisGameAndMap(String game, String map)
    {
        int temp = 0;

        for(ClientInfos infos : this.clients.values())
            if(infos.getDedicatedGame().equals(game))
                for(MinecraftServerInfos serverInfos : infos.getServersInfos())
                    if(serverInfos.getMap().equals(map))
                        temp++;

        return temp;
    }

    public KeepUpdatedThread getKeepUpdatedThread()
    {
        return this.keepUpdatedThread;
    }

    public HashMap<String, ClientInfos> getClients()
    {
        return this.clients;
    }
}
