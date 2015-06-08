package net.samagames.hydroangeas.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.samagames.hydroangeas.common.informations.ClientInfos;
import net.samagames.hydroangeas.common.informations.MinecraftServerInfos;
import net.samagames.hydroangeas.server.packets.MinecraftServerPacket;
import net.samagames.hydroangeas.utils.InstanceType;
import net.samagames.hydroangeas.utils.InternetUtils;
import net.samagames.hydroangeas.utils.ModMessage;

import java.util.ArrayList;
import java.util.logging.Level;

public class AlgorithmicMachine
{
    private final HydroangeasServer instance;
    private JsonArray lastData;

    public AlgorithmicMachine(HydroangeasServer instance)
    {
        this.instance = instance;
    }

    public void startMachinery()
    {
        ModMessage.sendMessage(InstanceType.SERVER, "> Etape 2 : Traitement des données...");
        this.check();
        ModMessage.sendMessage(InstanceType.SERVER, "> Prêt !");

        Thread work = new Thread(() ->
        {
            while(true)
            {
                try
                {
                    Thread.sleep(1000 * 15);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                this.check();
            }
        });

        work.start();
    }

    public void check()
    {
        String pageURL = this.instance.getConfiguration().getJsonConfiguration().get("web-domain").getAsString() + "games.php";

        if(InternetUtils.readURL(pageURL) != null)
            this.lastData = new JsonParser().parse(InternetUtils.readURL(pageURL)).getAsJsonArray();

        if(this.lastData == null)
        {
            this.instance.log(Level.SEVERE, "Can't get the game list for the AlgorithmicMachine!");
            ModMessage.sendError(InstanceType.SERVER, "Impossible d'obtenir la liste des jeux pour l'AlgorithmicMachine !");

            return;
        }

        for(int i = 0; i < this.lastData.size(); i++)
        {
            JsonObject jsonGame = this.lastData.get(i).getAsJsonObject();
            String game = jsonGame.get("game").getAsString();
            String map = jsonGame.get("map").getAsString();
            int instances = jsonGame.get("instances").getAsInt();

            ArrayList<ClientInfos> clients = this.instance.getClientManager().getClientsByGame(game);

            if(clients.isEmpty())
            {
                this.instance.log(Level.WARNING, "No clients available for the game '" + game + "' !");
                continue;
            }

            ClientInfos clientSelected = null;

            for(int j = 0; j < clients.size(); j++)
            {
                ClientInfos infos = clients.get(j);

                if((infos.getServerInfos().size() / infos.getMaxInstances()) * 100 <= 70)
                {
                    clientSelected = infos;
                    break;
                }
                else if(j == (clients.size() - 1))
                {
                    clientSelected = infos;
                    this.instance.log(Level.WARNING, "A client is overcharged! (" + infos.getClientName() + " - " + (infos.getServerInfos().size() / infos.getMaxInstances()) * 100 + ")");
                    break;
                }
            }

            if(clientSelected == null)
            {
                this.instance.log(Level.WARNING, "No clients available for the game '" + game + "' !");
                continue;
            }

            for(int k = this.instance.getClientManager().getStartedServersForThisGameAndMap(game, map); k < instances; k++)
            {
                MinecraftServerInfos serverInfos = new MinecraftServerInfos(game, map);
                new MinecraftServerPacket(clientSelected, serverInfos).send();
            }
        }
    }
}
