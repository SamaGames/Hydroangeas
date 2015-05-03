package net.samagames.hydroangeas.server.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javafx.util.Pair;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public abstract class AbstractHttpHandler implements HttpHandler
{
    public final String error403message = "Forbidden";
    public final String error404message = "File not found";
    public final String error500message = "Internal Server Error";

    @Override
    public void handle(HttpExchange httpExchange) throws IOException
    {
        OutputStream os = httpExchange.getResponseBody();

        try
        {
            Pair<Integer, String> data = this.getData(httpExchange);

            httpExchange.sendResponseHeaders(data.getKey(), data.getValue().length());

            os.write(data.getValue().getBytes());
            os.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();

            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, this.error500message.length());
            os.write(this.error500message.getBytes());
            os.close();
        }
    }

    public abstract Pair<Integer, String> getData(HttpExchange httpExchange) throws IOException;

    public Pair<Integer, String> get403()
        {
        return new Pair<>(HttpURLConnection.HTTP_FORBIDDEN, this.error403message);
    }

    public Pair<Integer, String> get404()
    {
        return new Pair<>(HttpURLConnection.HTTP_NOT_FOUND, this.error404message);
    }

    public Pair<Integer, String> get500()
    {
        return new Pair<>(HttpURLConnection.HTTP_INTERNAL_ERROR, this.error500message);
    }
}
