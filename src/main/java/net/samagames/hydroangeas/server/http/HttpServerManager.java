package net.samagames.hydroangeas.server.http;

import com.sun.net.httpserver.HttpServer;
import net.samagames.hydroangeas.server.HydroangeasServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;

public class HttpServerManager
{
    private HttpServer httpServer;

    public HttpServerManager(HydroangeasServer instance)
    {
        instance.log(Level.INFO, "Starting web server...");

        try
        {
            this.httpServer = HttpServer.create(new InetSocketAddress(7878), 0);
            this.httpServer.createContext("/", new WelcomeHttpHandler());
            this.httpServer.setExecutor(null);
            this.httpServer.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        instance.log(Level.INFO, "Web server started.");
    }

    public void disable()
    {
        this.httpServer.stop(1);
    }

    public HttpServer getHttpServer()
    {
        return this.httpServer;
    }
}
