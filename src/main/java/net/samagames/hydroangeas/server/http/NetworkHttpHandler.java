package net.samagames.hydroangeas.server.http;

import com.sun.net.httpserver.HttpExchange;
import javafx.util.Pair;

import java.io.IOException;
import java.net.HttpURLConnection;

public class NetworkHttpHandler extends AbstractHttpHandler
{
    @Override
    public Pair<Integer, String> getData(HttpExchange httpExchange) throws IOException
    {
        return new Pair<>(HttpURLConnection.HTTP_OK, "");
    }
}
