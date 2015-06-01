package net.samagames.hydroangeas.server.http;

import com.sun.net.httpserver.HttpExchange;
import javafx.util.Pair;
import net.samagames.hydroangeas.utils.StatsUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Calendar;
import java.util.HashMap;

public class GraphHttpHandler extends AbstractHttpHandler
{
    @Override
    public Pair<Integer, String> getData(HttpExchange httpExchange) throws IOException
    {
        try
        {
            StringBuilder page = new StringBuilder();
            InputStream in = NetworkHttpHandler.class.getResourceAsStream("/public/graph.html");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;

            while ((line = reader.readLine()) != null)
            {
                page.append(line);
            }

            String finalPage = page.toString();
            HashMap<Integer, Integer> weeklyServersStats = StatsUtils.getWeeklyServersStats();

            finalPage = finalPage.replace("WEEKLY_SERVERS_MONDAY", String.valueOf(weeklyServersStats.get(Calendar.MONDAY)));
            finalPage = finalPage.replace("WEEKLY_SERVERS_TUESDAY", String.valueOf(weeklyServersStats.get(Calendar.TUESDAY)));
            finalPage = finalPage.replace("WEEKLY_SERVERS_WEDNESDAY", String.valueOf(weeklyServersStats.get(Calendar.WEDNESDAY)));
            finalPage = finalPage.replace("WEEKLY_SERVERS_THURSDAY", String.valueOf(weeklyServersStats.get(Calendar.THURSDAY)));
            finalPage = finalPage.replace("WEEKLY_SERVERS_FRIDAY", String.valueOf(weeklyServersStats.get(Calendar.FRIDAY)));
            finalPage = finalPage.replace("WEEKLY_SERVERS_SATURDAY", String.valueOf(weeklyServersStats.get(Calendar.SATURDAY)));
            finalPage = finalPage.replace("WEEKLY_SERVERS_SUNDAY", String.valueOf(weeklyServersStats.get(Calendar.SUNDAY)));

            HashMap<String, Integer> weeklyGamesStats = StatsUtils.getWeeklyGamesStats();
            String labels = "";
            String values = "";

            for(String game : weeklyGamesStats.keySet())
            {
                labels += "\"" + game + "\", ";
                values += "\"" + weeklyGamesStats.get(game) + "\", ";
            }

            finalPage = finalPage.replace("WEEKLY_GAMES_LABEL", labels);
            finalPage = finalPage.replace("WEEKLY_GAMES", values);

            return new Pair<>(HttpURLConnection.HTTP_OK, finalPage);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return this.get500();
    }
}
