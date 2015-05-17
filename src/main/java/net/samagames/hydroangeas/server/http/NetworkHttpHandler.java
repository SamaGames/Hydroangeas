package net.samagames.hydroangeas.server.http;

import com.sun.net.httpserver.HttpExchange;
import javafx.util.Pair;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.common.informations.ClientInfos;

import java.io.*;
import java.net.HttpURLConnection;

public class NetworkHttpHandler extends AbstractHttpHandler
{
    @Override
    public Pair<Integer, String> getData(HttpExchange httpExchange) throws IOException
    {
        try
        {
            StringBuilder page = new StringBuilder();
            OutputStream os = httpExchange.getResponseBody();
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
                clients.append("<tr>");
                clients.append("<td>").append(clientInfos.getClientUUID().toString()).append("</td>");
                clients.append("<td>").append(clientInfos.getIp()).append("</td>");
                clients.append("<td>").append(clientInfos.getDedicatedGame() == null ? "None" : clientInfos.getDedicatedGame()).append("</td>");
                clients.append("</tr>");
            }

            String finalPage = page.toString().replace("%CLIENTS%", clients);

            return new Pair<>(HttpURLConnection.HTTP_OK, finalPage);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return this.get500();
    }
}
