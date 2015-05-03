package net.samagames.hydroangeas.server.http;

import com.sun.net.httpserver.HttpExchange;
import javafx.util.Pair;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WelcomeHttpHandler extends AbstractHttpHandler
{
    @Override
    public Pair<Integer, String> getData(HttpExchange httpExchange) throws IOException
    {
        String path = httpExchange.getRequestURI().toString().replaceFirst("/", "/public/");

        if (path.equalsIgnoreCase("/public/"))
        {
            path += "index.html";
        }

        URL resource = WelcomeHttpHandler.class.getResource(path);
        InputStream in = WelcomeHttpHandler.class.getResourceAsStream(path);

        if (resource == null)
            return this.get404();
        else
            return new Pair<>(HttpURLConnection.HTTP_OK, IOUtils.toString(in, "UTF-8"));
    }
}
