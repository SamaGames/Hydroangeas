package net.samagames.hydroangeas.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.packets.MinecraftServerEndPacket;
import net.samagames.hydroangeas.common.informations.ClientInfos;
import net.samagames.hydroangeas.common.informations.MinecraftServerInfos;
import net.samagames.hydroangeas.common.informations.ServerGame;
import net.samagames.hydroangeas.server.packets.MinecraftServerPacket;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.InternetUtils;
import net.samagames.hydroangeas.utils.ModMessage;

import java.util.ArrayList;
import java.util.logging.Level;

public class AlgorithmicMachine
{
    private final HydroangeasServer instance;
    private final ArrayList<ServerGame> serversToCalc;
    private JsonArray lastData;
    private boolean initialized;

    public AlgorithmicMachine(HydroangeasServer instance)
    {
        this.instance = instance;
        this.serversToCalc = new ArrayList<>();
        this.initialized = false;
    }

    public void startMachinery()
    {
        ModMessage.sendMessage(InstanceType.SERVER, "> Prêt !");

        Thread updateList = new Thread(() ->
        {
            while(true)
            {
                String pageURL = this.instance.getConfiguration().getJsonConfiguration().get("web-domain").getAsString() + "games.json";

                if(InternetUtils.readFullURL(pageURL) != null)
                    this.lastData = new JsonParser().parse(InternetUtils.readFullURL(pageURL)).getAsJsonArray();

                if(this.lastData == null)
                {
                    this.instance.log(Level.SEVERE, "Can't get the game list for the AlgorithmicMachine!");
                    ModMessage.sendError(InstanceType.SERVER, "Impossible d'obtenir la liste des jeux pour l'AlgorithmicMachine !");

                    return;
                }

                this.serversToCalc.clear();

                for(int i = 0; i < this.lastData.size(); i++)
                {
                    JsonObject server = this.lastData.get(i).getAsJsonObject();
                    this.serversToCalc.add(new ServerGame(server.get("game").getAsString(), server.get("map").getAsString(), server.get("instances").getAsInt()));
                }

                if(!this.initialized)
                    initiate();

                try
                {
                    Thread.sleep(1000 * 60);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        updateList.start();
    }

    public void initiate()
    {
        for(ServerGame serverGame : this.serversToCalc)
        {
            int count = 0;

            for(ClientInfos clientInfos : Hydroangeas.getInstance().getAsServer().getClientManager().getClientsByGame(serverGame.getGame()))
            {
                for(MinecraftServerInfos serverInfos : clientInfos.getServersInfos())
                {
                    if(serverInfos.getMap().equals(serverGame.getMap()))
                        count++;
                }
            }

            if(count < serverGame.getInstances())
            {
                for(int i = count; i < serverGame.getInstances(); i++)
                {
                    MinecraftServerInfos serverInfos = new MinecraftServerInfos(serverGame.getGame(), serverGame.getMap());
                    ClientInfos selected = this.selectMostUseableClient(serverGame.getGame());

                    new MinecraftServerPacket(selected, serverInfos).send();
                }
            }
        }

        this.initialized = true;
    }

    public ClientInfos selectMostUseableClient(String game)
    {
        ArrayList<ClientInfos> clients = Hydroangeas.getInstance().getAsServer().getClientManager().getClientsByGame(game);
        ClientInfos selected = null;

        for(ClientInfos clientInfos : clients)
        {
            if((clientInfos.getServersInfos().size() / clientInfos.getMaxInstances()) * 100 <= 70)
            {
                selected = clientInfos;
                break;
            }
        }

        return selected;
    }

    public void onServerUpdate(MinecraftServerEndPacket serverStatus)
    {
        MinecraftServerInfos serverInfos = new MinecraftServerInfos(serverStatus.getServerInfos().getGame(), serverStatus.getServerInfos().getMap());
        ClientInfos client = this.selectMostUseableClient(serverInfos.getGame());

        if(client == null)
        {
            Hydroangeas.getInstance().log(Level.SEVERE, "No client available for the game '" + serverInfos.getGame() + "'!");
            ModMessage.sendError(InstanceType.SERVER, "Aucun client disponible pour le jeu '" + serverInfos.getGame() + "'!");

            return;
        }

        new MinecraftServerPacket(client, serverInfos).send();
    }
}
