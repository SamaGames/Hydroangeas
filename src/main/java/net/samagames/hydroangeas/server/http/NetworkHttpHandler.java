package net.samagames.hydroangeas.server.http;

import com.sun.net.httpserver.HttpExchange;
import javafx.util.Pair;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.informations.ClientInfos;
import net.samagames.hydroangeas.common.informations.MinecraftServerInfos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class NetworkHttpHandler extends AbstractHttpHandler
{
    @Override
    public Pair<Integer, String> getData(HttpExchange httpExchange) throws IOException
    {
        try
        {
            StringBuilder page = new StringBuilder();
            InputStream in = NetworkHttpHandler.class.getResourceAsStream("/public/network.html");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;

            while ((line = reader.readLine()) != null)
            {
                page.append(line);
            }

            StringBuilder clients = new StringBuilder();

            for(ClientInfos clientInfos : Hydroangeas.getInstance().getAsServer().getClientManager().getClients().values())
            {
                String dedicatedGame = (clientInfos.getDedicatedGame() == null ? "Aucun jeu dédié" : clientInfos.getDedicatedGame());
                clients.append("[{v:'").append(clientInfos.getIp()).append("', f:'").append(clientInfos.getIp()).append("<div style=\"color:green;\">Hydroangeas Client</div><div style=\"color:yellow;\">" + dedicatedGame + "</div>'}, 'Hydroangeas Server'],");

                if(!clientInfos.getServerInfos().isEmpty())
                    for(MinecraftServerInfos serverInfos : clientInfos.getServerInfos())
                        clients.append("['").append(serverInfos.getServerName()).append("', '").append(clientInfos.getIp()).append("']");
            }

            String finalPage = page.toString().replace("%DATA%", clients);

            return new Pair<>(HttpURLConnection.HTTP_OK, finalPage);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return this.get500();
    }
}
