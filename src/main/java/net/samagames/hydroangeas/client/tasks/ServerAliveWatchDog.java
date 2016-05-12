package net.samagames.hydroangeas.client.tasks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.samagames.hydroangeas.Hydroangeas;
import net.samagames.hydroangeas.client.HydroangeasClient;
import net.samagames.hydroangeas.client.servers.MinecraftServerC;
import net.samagames.hydroangeas.utils.ping.MinecraftPing;
import net.samagames.hydroangeas.utils.ping.MinecraftPingOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Silvanosky on 10/01/2016.
 */
public class ServerAliveWatchDog extends Thread
{
    //private ScheduledExecutorService executor;
    private HydroangeasClient instance;

    public ServerAliveWatchDog(HydroangeasClient instance)
    {
        this.instance = instance;
        //this.executor = Executors.newScheduledThreadPool(3);

        //Minecraft vanilla ping
        instance.getScheduler().scheduleAtFixedRate(() -> {
            try{
                List<MinecraftServerC> servers = new ArrayList<>();
                servers.addAll(instance.getServerManager().getServers());
                for(MinecraftServerC server : servers) {
                    if(System.currentTimeMillis() - server.getStartedTime() < 20000L)
                        continue;
                    try {
                        String ip = HydroangeasClient.getInstance().getAsClient().getIP();
                        new MinecraftPing().getPing(new MinecraftPingOptions().setHostname(ip).setPort(server.getPort()).setTimeout(100));
                    } catch (IOException e) {
                        Hydroangeas.getInstance().getLogger().info("Can't ping server: " + server.getServerName() + " shutting down");
                        server.stopServer();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }, 15, 15, TimeUnit.SECONDS);

        //Check data and react
        instance.getScheduler().scheduleAtFixedRate(() -> {
            try{
                List<MinecraftServerC> servers = new ArrayList<>();
                servers.addAll(instance.getServerManager().getServers());
                for(MinecraftServerC server : servers)
                {
                    if(System.currentTimeMillis() - server.getStartedTime() < 20000L)
                        continue;

                    if(System.currentTimeMillis() - server.getLastHeartbeat() > 8000L)
                    {
                        Hydroangeas.getInstance().getLogger().info("Docker container seems to be offline for: " + server.getServerName() + " shutting down");
                        server.stopServer();
                    }
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }, 15, 2, TimeUnit.SECONDS);

        //Get data from docker
        instance.getScheduler().scheduleAtFixedRate(() -> {
            try{
                JsonArray containers = instance.getDockerAPI().listRunningContainers();

                if(containers != null)
                {
                    for(JsonElement object : containers)
                    {
                        try{
                            if(object.isJsonObject())
                            {
                                JsonObject container = object.getAsJsonObject();
                                String name = container.get("Names").getAsJsonArray().get(0).getAsString().replaceFirst("/", "");
                                MinecraftServerC serverByName = instance.getServerManager().getServerByName(name);

                                if(serverByName != null)
                                {
                                    serverByName.doHeartbeat();
                                }
                            }
                        }catch (Exception e)
                        {

                        }
                    }
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }, 10, 1, TimeUnit.SECONDS);
    }

    public void disable()
    {
        //executor.shutdownNow();
    }
}
